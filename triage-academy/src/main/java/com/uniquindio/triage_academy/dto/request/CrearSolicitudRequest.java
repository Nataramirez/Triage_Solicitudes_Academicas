package com.uniquindio.triage_academy.dto.request;


import com.uniquindio.triage_academy.model.enums.CanalOrigen;
import com.uniquindio.triage_academy.model.enums.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CrearSolicitudRequest {
    @NotNull
    private UUID idUsuario;

    @NotNull
    private TipoSolicitud tipo;

    @NotBlank
    private String descripcion;

    @NotNull
    private CanalOrigen canalOrigen;
}
