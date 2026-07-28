package com.endos.book.exception;

// Import Jackson untuk exclude field kosong dari JSON response
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

// DTO untuk response error — dikirim ke frontend saat terjadi exception
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // Jangan include field kosong/null di JSON
public class ExceptionResponse {

    private Integer businessErrorCode;      // Kode error bisnis (300, 301, ...)
    private String businessErrorDescription; // Deskripsi error bisnis
    private String error;                    // Pesan error umum
    private Set<String> validationError;     // Kumpulan error validasi (contoh: "Email is mandatory")
    private Map<String,String> errors;       // Map error field → pesan
}
