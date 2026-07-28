package com.endos.book.service;

// Import DTOs, entity, dan Spring Security
import com.endos.book.dto.request.AuthenticationRequest;
import com.endos.book.dto.request.RegistrationRequest;
import com.endos.book.dto.response.AuthenticationResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Interface untuk layanan autentikasi — implementasi di AuthServiceImpl
public interface AuthService {

    // Registrasi user baru → simpan ke DB, kirim email aktifasi, return tokens
    AuthenticationResponse register(RegistrationRequest request) throws MessagingException;

    // Login → validasi credentials, return access + refresh token + roles
    AuthenticationResponse authenticate(AuthenticationRequest request);

    // Aktifasi akun pakai token dari email → set enabled=true
    void activateAccount(String token) throws MessagingException;

    // Refresh access token pakai refresh token dari request header
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
