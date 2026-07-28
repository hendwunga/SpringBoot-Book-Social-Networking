package com.endos.book.security;

// Import JJWT library dan Spring Security
import com.endos.book.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Service untuk membuat dan validasi JWT tokens
@Getter
@Setter
@Service
@RequiredArgsConstructor
public class JwtService {

    // Secret key untuk sign JWT — dari application.yml
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // Masa berlaku access token (ms) — dari application.yml
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // Masa berlaku refresh token (ms) — dari application.yml
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long jwtRefreshTokenExpiration;

    // Extract email (subject) dari JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Subject = email
    }

    // Extract claim tertentu dari JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Decode JWT + verifikasi signature
        return claimsResolver.apply(claims);           // Ekstrak claim yang diinginkan
    }

    // Generate access token tanpa extra claims
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generate access token dengan extra claims (contoh: fullName)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    // Build JWT token dengan claims, subject, expiration, authorities
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        // Ambil authorities: ["USER", "ADMIN"]
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setClaims(extraClaims)                     // Extra claims (fullName)
                .setSubject(userDetails.getUsername())       // Subject = email
                .setIssuedAt(new Date())                    // Waktu dibuat
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Waktu expired
                .claim("authorities", authorities)          // Simpan roles di payload
                .signWith(getSignInKey())                   // Sign dengan HMAC-SHA secret key
                .compact();                                // Generate JWT string
    }

    // Generate refresh token (UUID random, masa berlaku lebih lama)
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtRefreshTokenExpiration);
    }

    // Cek apakah JWT valid untuk user ini
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Email harus sama DAN token belum expired
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Cek apakah refresh token valid
    public boolean isRefreshTokenValid(String refreshToken, UserDetails userDetails) {
        return isTokenValid(refreshToken, userDetails);
    }

    // Generate access token baru dari refresh token
    public String refreshAccessToken(String refreshToken, UserDetails userDetails) {
        if (!isRefreshTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        return generateToken(userDetails);
    }

    // Cek apakah token sudah expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // expiration < now
    }

    // Extract claim expiration dari JWT
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Decode dan verifikasi JWT → return semua claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Decode secret key
                .build()
                .parseClaimsJws(token)        // Verifikasi signature + decode
                .getBody();                   // Return claims
    }

    // Decode secret key dari Base64 → HMAC key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes); // HMAC-SHA key
    }
}
