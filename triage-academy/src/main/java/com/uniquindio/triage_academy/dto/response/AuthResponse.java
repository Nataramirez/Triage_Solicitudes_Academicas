package com.uniquindio.triage_academy.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UsuarioResponse usuario;
}
