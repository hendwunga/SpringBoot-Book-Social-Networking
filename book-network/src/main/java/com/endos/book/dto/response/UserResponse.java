package com.endos.book.dto.response;

// Import Lombok dan Java time
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// DTO response untuk data user — dikirim ke frontend di GET /users
@Data       // Auto-generate getter, setter, toString, equals, hashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Integer id;              // ID user
    private String firstname;        // Nama depan
    private String lastname;         // Nama belakang
    private String email;            // Email (username)
    private boolean accountLocked;   // Status kunci akun
    private boolean enabled;         // Status aktif/aktifasi
    private List<String> roles;      // Daftar role (contoh: ["USER", "ADMIN"])
    private LocalDateTime createdDate; // Waktu registrasi
}
