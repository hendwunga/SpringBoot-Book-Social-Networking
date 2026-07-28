package com.endos.book.exception;

// Import semua exception types yang ditangani
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.endos.book.exception.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

// Global exception handler — menangani semua exception dan return response JSON yang konsisten
@RestControllerAdvice  // Berlaku untuk semua @RestController
public class GlobalExceptionHandler {

    // Ditangani saat user mencoba login dengan akun yang dikunci
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode())       // 302
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    // Ditangani saat user mencoba login dengan akun yang belum aktifasi
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode())     // 303
                                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    // Ditangani saat login dengan email/password salah
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode())      // 304
                                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error(BAD_CREDENTIALS.getDescription())
                                .build()
                );
    }

    // Ditangani saat pengiriman email gagal
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    // Ditangani saat user mencoba operasi yang tidak diizinkan (contoh: pinjam buku sendiri)
    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    // Ditangani saat validasi @Valid gagal (contoh: email kosong, password kurang dari 8 karakter)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors=new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error-> {
                    var errorMessage = error.getDefaultMessage(); // Pesan dari @NotEmpty, @Email, dll
                    errors.add(errorMessage);
                });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationError(errors) // Set<String> semua error validasi
                                .build()
                );
    }


    // Ditangani saat access token JWT expired
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleException(ExpiredJwtException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(JWT_EXPIRED.getCode())          // 305
                                .businessErrorDescription(JWT_EXPIRED.getDescription())
                                .error("Access token has expired. Please use your refresh token to get a new access token")
                                .build()
                );
    }

    // Ditangani saat JWT tidak valid (signature salah, format salah)
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse> handleException(JwtException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(JWT_INVALID.getCode())          // 306
                                .businessErrorDescription(JWT_INVALID.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }


    // Ditangani untuk semua exception lain yang tidak tertangani di atas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace(); // Log stack trace ke console
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal error,contact the admin")
                                .error(exp.getMessage())
                                .build()
                );
    }
}
