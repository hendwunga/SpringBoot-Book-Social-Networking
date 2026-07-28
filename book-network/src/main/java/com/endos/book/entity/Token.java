package com.endos.book.entity;

// Import JPA, Lombok
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Entity token JWT — menyimpan access token dan refresh token di database
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String token;               // Access token JWT

    @Column(unique = true)
    private String refreshToken;        // Refresh token (UUID) untuk minta token baru

    private LocalDateTime createdAt;    // Kapan token dibuat
    private LocalDateTime expiresAt;    // Kapan access token expired
    private LocalDateTime refreshExpiresAt; // Kapan refresh token expired
    private LocalDateTime validatedAt;  // Kapan token diverifikasi/aktif

    // Token milik user mana
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    private boolean revoked;    // True = token dicabut (saat login baru, token lama di-revoke)
    private boolean expired;    // True = token sudah kadaluarsa
}
