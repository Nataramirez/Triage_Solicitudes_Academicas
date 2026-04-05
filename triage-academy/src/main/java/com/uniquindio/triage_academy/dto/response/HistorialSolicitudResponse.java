package com.uniquindio.triage_academy.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class HistorialSolicitudResponse {
    private UUID id;
    private UUID idSolicitud;
    private LocalDateTime fechaCreacion;
    private String accion;
    private String observaciones;
}
