package com.uniquindio.triage_academy.dto.request;

import com.uniquindio.triage_academy.model.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarUsuarioRequest {

    @NotBlank(message = "La identificación del usuario es requerida")
    private String identificacion;

    @NotBlank(message = "El nombre del usuario es requerido")
    private String nombre;

    @Email(message = "El correo electrónico proporcionado no es válido")
    @NotBlank(message = "El correo electrónico del usuario es requerido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    private String contrasena;

    @NotNull(message = "El rol es requerido")
    private RolUsuario rol;

    @Override
    public String toString() {
        return "RegistrarUsuarioRequest{" +
                "identificacion='" + identificacion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", rol=" + rol +
                '}';
    }
}
