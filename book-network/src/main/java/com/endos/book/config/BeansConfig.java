package com.endos.book.config;

// Import Spring Security beans dan CORS
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.*;

// Konfigurasi beans utama: authentication, CORS, password encoder
@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    private final UserDetailsService userDetailsService; // Load user dari DB

    // Bean AuthenticationProvider — verifikasi email + password pakai BCrypt
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Load user dari DB
        authProvider.setPasswordEncoder(passwordEncoder());    // Verifikasi pakai BCrypt
        return authProvider;
    }

    // Bean AuthenticationManager — dipakai di AuthServiceImpl untuk login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Bean AuditorAware — isi otomatis @CreatedBy dan @LastModifiedBy
    @Bean
    public AuditorAware<Integer> auditorAware() {
        return new ApplicationAuditAware();
    }

    // Bean PasswordEncoder — hash password pakai BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean CorsFilter — izinkan request dari Angular (port 4200)
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Izinkan credentials (cookies, headers)
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // Frontend Angular
        config.setAllowedHeaders(Arrays.asList(
                ORIGIN,         // Header Origin
                CONTENT_TYPE,   // Content-Type
                ACCEPT,         // Accept
                AUTHORIZATION)); // Authorization (JWT token)
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "DELETE", "PUT", "PATCH")); // HTTP methods yang diizinkan

        source.registerCorsConfiguration("/**", config); // Apply ke semua path
        return new CorsFilter(source);
    }
}
