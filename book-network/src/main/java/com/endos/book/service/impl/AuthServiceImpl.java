package com.endos.book.service.impl;

// Import semua dependency yang dibutuhkan
import com.endos.book.common.EmailTemplateName;
import com.endos.book.dto.request.AuthenticationRequest;
import com.endos.book.dto.request.RegistrationRequest;
import com.endos.book.dto.response.AuthenticationResponse;
import com.endos.book.entity.Token;
import com.endos.book.entity.User;
import com.endos.book.repository.RoleRepository;
import com.endos.book.repository.TokenRepository;
import com.endos.book.repository.UserRepository;
import com.endos.book.security.JwtService;
import com.endos.book.service.AuthService;
import com.endos.book.service.EmailService;

import com.fasterxml.jackson.databind.ObjectMapper; // Untuk write JSON ke response output stream
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

// Implementasi AuthService — menangani register, login, aktifasi, refresh token
@Service
@RequiredArgsConstructor   // Auto-generate constructor untuk semua field final
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;        // Akses tabel user
    private final PasswordEncoder passwordEncoder;      // Hash password dengan BCrypt
    private final JwtService jwtService;                // Buat & validasi JWT
    private final AuthenticationManager authenticationManager; // Verifikasi email + password
    private final RoleRepository roleRepository;        // Akses tabel role
    private final EmailService emailService;            // Kirim email aktifasi
    private final TokenRepository tokenRepository;      // Simpan/cari JWT di DB

    // URL frontend untuk aktifasi akau — dari application.yml
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    // ========== REGISTRASI ==========
    @Override
    public AuthenticationResponse register(RegistrationRequest request) throws MessagingException {
        // 1. Cari role USER di database (harus sudah di-insert saat startup)
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));

        // 2. Buat user baru dengan password yang sudah di-hash
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .accountLocked(false)   // Akun tidak dikunci
                .enabled(false)         // Belum aktif — harus klik link di email
                .roles(Set.of(userRole))// Default role: USER
                .build();
        userRepository.save(user); // Simpan ke database

        // 3. Kirim email aktifasi berisi kode OTP 6 digit
        sendValidationEmail(user);

        // 4. Generate JWT tokens untuk response (user bisa langsung login)
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getFullName()); // Simpan nama di payload JWT

        var jwtToken = jwtService.generateToken(claims, user);     // Access token
        var refreshToken = jwtService.generateRefreshToken(user);  // Refresh token

        // 5. Simpan tokens ke database (untuk tracking & revoke)
        saveUserToken(user, jwtToken, refreshToken);

        // 6. Return tokens ke frontend
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    // ========== LOGIN ==========
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Verifikasi email + password pakai Spring AuthenticationManager
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Ambil data user dari hasil autentikasi
        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal(); // Cast ke entity User
        claims.put("fullName", user.getFullName());

        // 3. Revoke semua token lama user ini (agar hanya token terbaru yang aktif)
        revokeAllUserTokens(user);

        // 4. Generate token baru
        var accessToken = jwtService.generateToken(claims, user);
        var refreshToken = generateRefreshToken(); // UUID random
        saveUserToken(user, accessToken, refreshToken);

        // 5. Return tokens + roles ke frontend
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(user.getRoles().stream()
                        .map(role -> role.getName()) // Konversi Set<Role> → List<String>
                        .collect(Collectors.toList()))
                .build();
    }


    // ========== AKTIFASI AKUN ==========
    @Transactional  // Satu transaksi: update user + token
    @Override
    public void activateAccount(String token) throws MessagingException {
        // 1. Cari token di database
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // 2. Cek apakah token sudah expired
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            // Token expired → kirim email baru
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        // 3. Aktifkan akun user (set enabled = true)
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        // 4. Tandai token sudah diverifikasi
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    // Kirim email aktifasi ke user baru
    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user); // Generate kode OTP 6 digit

        emailService.sendEmail(
                user.getEmail(),              // Tujuan email
                user.getFullName(),           // Nama lengkap (ditampilkan di template)
                EmailTemplateName.ACTIVATE_ACCOUNT, // Template: activate_account.html
                activationUrl,                // URL frontend: http://localhost:4200/activate-account
                newToken,                     // Kode OTP 6 digit
                "Account activation"          // Subjek email
        );
    }

    // Generate kode OTP 6 digit dan simpan ke database
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6); // 6 digit angka
        Token token = Token.builder()
                .token(generatedToken)              // Kode OTP
                .createdAt(LocalDateTime.now())     // Waktu dibuat
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // Expired dalam 15 menit
                .user(user)                         // Milik user ini
                .build();
        tokenRepository.save(token); // Simpan ke database

        return generatedToken;
    }

    // Generate kode OTP acak (angka saja)
    private String generateActivationCode(int length) {
        String characters = "0123456789"; // Hanya angka
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom(); // Cryptographically secure random

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString(); // Contoh: "847291"
    }


    // Simpan token (access + refresh) ke database
    private void saveUserToken(User user, String accessToken, String refreshToken) {
        Token tokenEntity = Token.builder()
                .token(accessToken)                     // Access token JWT
                .refreshToken(refreshToken)             // Refresh token (UUID)
                .createdAt(LocalDateTime.now())         // Waktu dibuat
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getJwtExpiration() / 1000)) // Expired
                .refreshExpiresAt(LocalDateTime.ofInstant(Instant.now().plusMillis(jwtService.getJwtRefreshTokenExpiration()), ZoneId.systemDefault()))
                .validatedAt(LocalDateTime.now())       // Langsung aktif
                .user(user)                             // Milik user ini
                .revoked(false)                         // Belum dicabut
                .expired(false)                         // Belum expired
                .build();

        tokenRepository.save(tokenEntity);
    }

    // Revoke (cabut) semua token aktif user — agar hanya token terbaru yang berlaku
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return; // Tidak ada token aktif → skip
        validUserTokens.forEach(token -> {
            token.setExpired(true);   // Tandai expired
            token.setRevoked(true);   // Tandai dicabut
        });
        tokenRepository.saveAll(validUserTokens); // Update semua sekaligus
    }

    // ========== REFRESH TOKEN ==========
    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 1. Ambil refresh token dari header Authorization: Bearer <refresh_token>
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // Tidak ada refresh token → return kosong
        }
        final String refreshToken = authHeader.substring(7); // Hapus "Bearer "

        // 2. Cari token di database
        var savedToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElse(null);

        if (savedToken == null || savedToken.isExpired() || savedToken.isRevoked()) {
            return; // Refresh token tidak valid → return kosong
        }

        // 3. Generate token baru
        var user = savedToken.getUser();

        revokeAllUserTokens(user); // Revoke token lama

        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = generateRefreshToken();
        saveUserToken(user, newAccessToken, newRefreshToken); // Simpan token baru

        // 4. Tulis response langsung ke output stream (bukan pakai ResponseEntity)
        var authResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList()))
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    // Generate refresh token sebagai UUID acak
    private String generateRefreshToken() {
        return UUID.randomUUID().toString(); // Contoh: "550e8400-e29b-41d4-a716-446655440000"
    }
}
