package com.uniquindio.triage_academy.service;

import com.uniquindio.triage_academy.dto.request.*;
import com.uniquindio.triage_academy.dto.response.*;
import com.uniquindio.triage_academy.model.enums.*;

import java.util.List;
import java.util.UUID;

public interface SolicitudInterface {
    SolicitudResponse crear(CrearSolicitudRequest request);
    List<SolicitudResponse> listar(EstadoSolicitud estado, TipoSolicitud tipo,
                                   Prioridad prioridad, UUID idResponsable);
    SolicitudResponse obtenerPorId(UUID id);
    void cambiarEstado(UUID id, CambiarEstadoRequest request);
    void cerrar(UUID id, CerrarSolicitudRequest request);
    List<HistorialSolicitudResponse> obtenerHistorial(UUID id);
}
