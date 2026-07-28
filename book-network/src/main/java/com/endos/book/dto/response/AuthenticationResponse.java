package com.endos.book.dto.response;

// Import Lombok
import lombok.*;

import java.util.List;

// DTO response setelah login/register — dikirim ke frontend
@Data       // Auto-generate getter, setter, toString, equals, hashCode
@Builder    // Supaya bisa pakai AuthenticationResponse.builder().accessToken("xxx")...
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String accessToken;    // JWT access token — frontend simpan di localStorage
    private String refreshToken;   // JWT refresh token — frontend pakai saat access token expired
    private List<String> roles;    // Daftar role user (contoh: ["USER", "ADMIN"])
}
