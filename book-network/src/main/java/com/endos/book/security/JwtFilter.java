package com.endos.book.security;

// Import dependency
import com.endos.book.entity.Token;
import com.endos.book.repository.TokenRepository;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

// JWT Filter — intercept setiap request untuk validasi token
// Dipanggil SEBELUM request masuk ke controller
@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;              // Validasi JWT
    private final UserDetailsService userDetailsService; // Load user dari DB
    private final TokenRepository tokenRepository;    // Cek token di database

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,   // Request dari client
            @NonNull HttpServletResponse response,  // Response ke client
            @NonNull FilterChain filterChain        // Filter chain Spring Security
    ) throws ServletException, IOException {

        // 1. Ambil header Authorization dari request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Jika tidak ada header atau bukan "Bearer xxx" → skip filter ini
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Lanjut ke filter/controller berikutnya
            return;
        }

        // 3. Ambil JWT token (hapus "Bearer " di depan)
        jwt = authHeader.substring(7);

        try {
            // 4. Extract email dari JWT (subject claim)
            userEmail = jwtService.extractUsername(jwt);
        } catch (JwtException e) {
            // JWT tidak valid atau expired → skip, jangan set authentication
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Jika email ditemukan DAN belum ada authentication di context
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. Load user dari database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. Cek apakah token ada di database dan masih valid (tidak expired, tidak revoked)
            Optional<Token> tokenOpt = tokenRepository.findByToken(jwt);
            boolean isTokenValid = tokenOpt
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            // 8. Validasi JWT signature + cek token valid di DB
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                // 9. Buat authentication token dan set ke SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,    // Principal (User entity)
                        null,           // Credentials (sudah diverifikasi)
                        userDetails.getAuthorities() // Roles: ["USER", "ADMIN"]
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request) // IP, session ID
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); // Set authentication
            }
        }

        // 10. Lanjut ke filter/controller berikutnya
        filterChain.doFilter(request, response);
    }
}
