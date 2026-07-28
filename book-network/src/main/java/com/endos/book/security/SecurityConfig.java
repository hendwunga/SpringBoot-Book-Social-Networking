package com.endos.book.security;

// Import Spring Security config
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

// Konfigurasi keamanan Spring Security
@Configuration
@EnableWebSecurity               // Aktifkan Spring Security
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true) // Aktifkan @PreAuthorize di controller

public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider; // Provider autentikasi (BCrypt)
    private final JwtFilter jwtAuthFilter;                      // JWT filter

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                // Aktifkan CORS (cross-origin requests dari Angular di port 4200)
                .cors(withDefaults())
                // Nonaktifkan CSRF (tidak perlu untuk stateless JWT API)
                .csrf(AbstractHttpConfigurer::disable)
                // Atur endpoint mana yang boleh diakses tanpa login
                .authorizeHttpRequests(req->
                        req.requestMatchers(
                                "/auth/**",         // Register, login, activate, refresh
                                "/v2/api-docs",     // Swagger docs
                                "/v3/api-docs",     // Swagger docs v3
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",   // Swagger UI
                                "/webjars/**",
                                "/swagger-ui.html"
                        )
                                .permitAll()        // Tidak perlu autentikasi
                                .anyRequest()       // Endpoint lainnya
                                .authenticated()    // Harus login
                    )
                // Stateless: tidak simpan session di server (setiap request harus bawa token)
                .sessionManagement(session-> session.sessionCreationPolicy(STATELESS))
                // Set authentication provider (BCrypt password encoder)
                .authenticationProvider(authenticationProvider)
                // Tambahkan JWT filter SEBELUM UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
