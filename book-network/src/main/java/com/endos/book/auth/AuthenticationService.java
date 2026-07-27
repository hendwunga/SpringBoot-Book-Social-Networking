package com.endos.book.auth;

import com.endos.book.email.EmailService;
import com.endos.book.email.EmailTemplateName;
import com.endos.book.role.RoleRepository;
import com.endos.book.security.JwtService;
import com.endos.book.user.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public AuthenticationResponse register(RegistrationRequest request) throws MessagingException {
        // Temukan role "USER" dari role repository
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));

        // Buat user baru dan simpan ke repository
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)  // Pengguna belum aktif
                .roles(Set.of(userRole))  // Menggunakan Set
                .build();
        userRepository.save(user);

        // Kirim email verifikasi untuk aktivasi akun
        sendValidationEmail(user);

        // Generate claims (jika diperlukan) dan JWT Token untuk pengguna yang baru didaftarkan
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getFullName());

        // Menghasilkan token JWT untuk user baru
        var jwtToken = jwtService.generateToken(claims, user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Simpan token yang dihasilkan ke dalam basis data
        saveUserToken(user, jwtToken, refreshToken);

        // Mengembalikan AuthenticationResponse berisi token
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
        claims.put("fullName", user.getFullName());

        revokeAllUserTokens(user);

        var accessToken = jwtService.generateToken(claims, user);
        var refreshToken = generateRefreshToken();
        saveUserToken(user, accessToken, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList()))
                .build();
    }


    // Send Email
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }


    // Refresh Token
    // Save access and refresh tokens to the database
    private void saveUserToken(User user, String accessToken, String refreshToken) {
        Token tokenEntity = Token.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getJwtExpiration() / 1000)) // Atur masa berlaku token akses
                .refreshExpiresAt(LocalDateTime.ofInstant(Instant.now().plusMillis(jwtService.getJwtRefreshTokenExpiration()), ZoneId.systemDefault()))
                .validatedAt(LocalDateTime.now())
                .user(user)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(tokenEntity);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        final String refreshToken = authHeader.substring(7);

        var savedToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElse(null);

        if (savedToken == null || savedToken.isExpired() || savedToken.isRevoked()) {
            return;
        }

        var user = savedToken.getUser();

        revokeAllUserTokens(user);

        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = generateRefreshToken();
        saveUserToken(user, newAccessToken, newRefreshToken);

        var authResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList()))
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
