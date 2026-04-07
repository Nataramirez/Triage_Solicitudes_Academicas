package com.uniquindio.triage_academy.helpers.mappers;

import com.uniquindio.triage_academy.dto.response.UsuarioResponse;
import com.uniquindio.triage_academy.model.entity.Usuario;

public class HelperMappers {

    public static UsuarioResponse toUsuarioResponse(Usuario usuario) {

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .identificacion(usuario.getIdentificacion())
                .rol(usuario.getRol())
                .correo(usuario.getCorreo())
                .activo(usuario.isActivo())
                .build();
    }
}
