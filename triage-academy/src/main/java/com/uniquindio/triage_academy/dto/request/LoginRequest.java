package com.uniquindio.triage_academy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
    @Email
    @NotBlank
    private String correo;

    @NotBlank
    private String contrasena;

}
