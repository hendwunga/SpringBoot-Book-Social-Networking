package com.endos.book.service;

import com.endos.book.dto.request.AuthenticationRequest;
import com.endos.book.dto.request.RegistrationRequest;
import com.endos.book.dto.response.AuthenticationResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthService {

    AuthenticationResponse register(RegistrationRequest request) throws MessagingException;

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void activateAccount(String token) throws MessagingException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
