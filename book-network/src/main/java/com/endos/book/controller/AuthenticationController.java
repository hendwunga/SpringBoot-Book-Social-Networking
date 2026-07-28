package com.endos.book.controller;

// Import DTOs, service, dan annotations
import com.endos.book.dto.request.AuthenticationRequest;
import com.endos.book.dto.request.RegistrationRequest;
import com.endos.book.dto.response.AuthenticationResponse;
import com.endos.book.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

// REST Controller untuk autentikasi (register, login, aktifasi, refresh)
@RestController
@RequestMapping("auth")           // Base path: /api/v1/auth/*
@RequiredArgsConstructor
@Tag(name = "Authentication")    // Label di Swagger UI
public class AuthenticationController {

    private final AuthService service; // Inject AuthService

    // POST /auth/register — registrasi user baru
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED) // Return 202 Accepted
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request // Validasi: firstname, lastname, email, password
    ) throws MessagingException {
        service.register(request); // Simpan user + kirim email aktifasi
        return ResponseEntity.accepted().build(); // 202 tanpa body
    }

    // POST /auth/authenticate — login
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request // email + password
    ) {
        return ResponseEntity.ok(service.authenticate(request)); // Return tokens + roles
    }


    // GET /auth/activate-account?token=123456 — aktifasi akau dari email
    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token // Kode OTP 6 digit dari email
    ) throws MessagingException {
        service.activateAccount(token); // Set enabled=true
    }

    // POST /auth/refresh-token — refresh access token
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,   // Ambil refresh token dari header
            HttpServletResponse response  // Tulis response JSON langsung
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
