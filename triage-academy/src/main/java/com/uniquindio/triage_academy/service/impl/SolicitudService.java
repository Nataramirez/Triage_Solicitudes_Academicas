package com.uniquindio.triage_academy.service.impl;

import com.uniquindio.triage_academy.dto.request.*;
import com.uniquindio.triage_academy.dto.response.*;
import com.uniquindio.triage_academy.model.entity.*;
import com.uniquindio.triage_academy.model.enums.*;
import com.uniquindio.triage_academy.repository.*;
import com.uniquindio.triage_academy.service.SolicitudInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudService implements SolicitudInterface {
    private final SolicitudRepository solicitudRepository;
    private final HistorialRepository historialRepository;
    private final UsuarioRepository usuarioRepository;

    private static final Map<EstadoSolicitud, EstadoSolicitud> TRANSICIONES = Map.of(
            EstadoSolicitud.REGISTRADA, EstadoSolicitud.CLASIFICADA,
            EstadoSolicitud.CLASIFICADA, EstadoSolicitud.EN_ATENCION,
            EstadoSolicitud.EN_ATENCION, EstadoSolicitud.ATENDIDA
    );

    private static final Map<TipoSolicitud, Prioridad> PRIORIDAD_POR_TIPO = Map.of(
            TipoSolicitud.HOMOLOGACION, Prioridad.ALTA,
            TipoSolicitud.REGISTRO_ASIGNATURAS, Prioridad.MEDIA,
            TipoSolicitud.CANCELACION_ASIGNATURAS, Prioridad.MEDIA,
            TipoSolicitud.SOLICITUD_CUPOS, Prioridad.BAJA,
            TipoSolicitud.CONSULTA_ACADEMICA, Prioridad.BAJA
    );

    @Override
    @Transactional
    public SolicitudResponse crear(CrearSolicitudRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Prioridad prioridad = PRIORIDAD_POR_TIPO.get(request.getTipo());


        Solicitud solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setTipo(request.getTipo());
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setCanalOrigen(request.getCanalOrigen());
        solicitud.setEstado(EstadoSolicitud.REGISTRADA);
        solicitud.setPrioridad(prioridad);
        solicitud.setJustificacionPrioridad("Asignada automáticamente por tipo: " +
                request.getTipo());
        solicitud.setFechaRegistro(LocalDateTime.now());
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, "CREACION", "Solicitud registrada en el sistema");

        return toResponse(solicitud);
    }

    @Override
    public List<SolicitudResponse> listar(EstadoSolicitud estado, TipoSolicitud tipo,
                                          Prioridad prioridad, UUID idResponsable) {
        Solicitud solicitud = new Solicitud();
        Specification<Solicitud> spec = Specification.where(
                (root, query, cb) -> cb.conjunction()
        );

        if (estado != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }
        if (tipo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        if (prioridad != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("prioridad"), prioridad));
        }
        if (idResponsable != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("responsable").get("id"), idResponsable));
        }



        return solicitudRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SolicitudResponse obtenerPorId(UUID id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
        return toResponse(solicitud);
    }

    @Override
    @Transactional
    public void cambiarEstado(UUID id, CambiarEstadoRequest request) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        EstadoSolicitud estadoActual = solicitud.getEstado();
        EstadoSolicitud nuevoEstado = request.getNuevoEstado();
        EstadoSolicitud siguienteValido = TRANSICIONES.get(estadoActual);

        if (!nuevoEstado.equals(siguienteValido)) {
            throw new IllegalStateException(
                    "Transición inválida: " + estadoActual + " → " + nuevoEstado);
        }

        solicitud.setEstado(nuevoEstado);
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, nuevoEstado.name(), request.getObservaciones());
    }

    @Override
    @Transactional
    public void cerrar(UUID id, CerrarSolicitudRequest request) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitud.ATENDIDA) {
            throw new IllegalStateException(
                    "Solo se pueden cerrar solicitudes en estado ATENDIDA");
        }

        solicitud.setEstado(EstadoSolicitud.CERRADA);
        solicitud.setObservacionCierre(request.getObservacionCierre());
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, "CERRADA", request.getObservacionCierre());
    }

    @Override
    public List<HistorialSolicitudResponse> obtenerHistorial(UUID id) {
        if (!solicitudRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitud no encontrada");
        }

        return historialRepository.findBySolicitudId(id).stream()
                .map(this::toHistorialResponse)
                .toList();
    }

    private void registrarHistorial(Solicitud solicitud, String accion, String observaciones) {
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setSolicitud(solicitud);
        historial.setAccion(accion);
        historial.setObservaciones(observaciones);
        historial.setFechaCreacion(LocalDateTime.now());
        historialRepository.save(historial);
    }

    private SolicitudResponse toResponse(Solicitud s) {
        return SolicitudResponse.builder()
                .id(s.getId())
                .idUsuario(s.getUsuario() != null ? s.getUsuario().getId() : null)
                .idResponsable(s.getResponsable() != null ? s.getResponsable().getId() : null)
                .tipo(s.getTipo())
                .descripcion(s.getDescripcion())
                .canalOrigen(s.getCanalOrigen())
                .fechaRegistro(s.getFechaRegistro())
                .estado(s.getEstado())
                .prioridad(s.getPrioridad())
                .justificacionPrioridad(s.getJustificacionPrioridad())
                .observacionCierre(s.getObservacionCierre())
                .build();
    }

    private HistorialSolicitudResponse toHistorialResponse(HistorialSolicitud h) {
        return HistorialSolicitudResponse.builder()
                .id(h.getId())
                .idSolicitud(h.getSolicitud().getId())
                .fechaCreacion(h.getFechaCreacion())
                .accion(h.getAccion())
                .observaciones(h.getObservaciones())
                .build();
    }
}