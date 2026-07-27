package com.endos.book.auth;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {


    private String accessToken;
    private String refreshToken;
    private List<String> roles;
}
