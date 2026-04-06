package com.uniquindio.triage_academy.dto.response;

import com.uniquindio.triage_academy.model.enums.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SolicitudResponse {
    private UUID id;
    private UUID idUsuario;
    private UUID idResponsable;
    private TipoSolicitud tipo;
    private String descripcion;
    private CanalOrigen canalOrigen;
    private LocalDateTime fechaRegistro;
    private EstadoSolicitud estado;
    private Prioridad prioridad;
    private String justificacionPrioridad;
    private String observacionCierre;
}
