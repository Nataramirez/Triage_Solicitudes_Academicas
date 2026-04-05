package com.uniquindio.triage_academy.dto.response;

import com.uniquindio.triage_academy.model.enums.RolUsuario;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class UsuarioResponse {
    private UUID id;
    private String identificacion;
    private String nombre;
    private String correo;
    private RolUsuario rol;
    private boolean activo;

}
