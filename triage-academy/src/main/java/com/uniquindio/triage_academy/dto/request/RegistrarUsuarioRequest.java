package com.uniquindio.triage_academy.dto.request;

import com.uniquindio.triage_academy.model.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RegistrarUsuarioRequest {
    @NotBlank
    private String identificacion;

    @NotBlank
    private String nombre;

    @Email
    @NotBlank
    private String correo;

    @NotBlank
    private String contrasena;

    @NotNull
    private RolUsuario rol;
}
