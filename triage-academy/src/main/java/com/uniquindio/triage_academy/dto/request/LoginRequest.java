package com.uniquindio.triage_academy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
public class LoginRequest {

    @Email(message = "El correo eléctronico proporcionado no es válido")
    @NotBlank(message = "El correo electrónico es requerido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    private String contrasena;

}
