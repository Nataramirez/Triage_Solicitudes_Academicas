package com.uniquindio.triage_academy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrarAdministrativoRequest {

    @NotBlank(message = "La identificación del usuario es requerida")
    private String identificacion;

    @NotBlank(message = "El nombre del usuario es requerido")
    private String nombre;

    @Email(message = "El correo electrónico proporcionado no es válido")
    @NotBlank(message = "El correo electrónico del usuario es requerido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    private String contrasena;

    @Override
    public String toString() {
        return "RegistrarAdministrativoRequest{" +
                "identificacion='" + identificacion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
