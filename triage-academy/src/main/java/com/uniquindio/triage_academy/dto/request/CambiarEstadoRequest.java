package com.uniquindio.triage_academy.dto.request;

import com.uniquindio.triage_academy.model.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CambiarEstadoRequest {
    @NotNull
    private EstadoSolicitud nuevoEstado;

    private String observaciones;
}
