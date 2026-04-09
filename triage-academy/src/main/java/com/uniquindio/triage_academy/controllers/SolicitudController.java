package com.uniquindio.triage_academy.controllers;

import com.uniquindio.triage_academy.dto.request.CambiarEstadoRequest;
import com.uniquindio.triage_academy.dto.request.CerrarSolicitudRequest;
import com.uniquindio.triage_academy.dto.request.CrearSolicitudRequest;
import com.uniquindio.triage_academy.dto.response.HistorialSolicitudResponse;
import com.uniquindio.triage_academy.dto.response.SolicitudResponse;
import com.uniquindio.triage_academy.model.enums.EstadoSolicitud;
import com.uniquindio.triage_academy.model.enums.Prioridad;
import com.uniquindio.triage_academy.model.enums.TipoSolicitud;
import com.uniquindio.triage_academy.service.SolicitudInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudInterface solicitudService;

    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(
            @Valid @RequestBody CrearSolicitudRequest request) {
        SolicitudResponse response = solicitudService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> listar(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) UUID idResponsable) {
        return ResponseEntity.ok(solicitudService.listar(estado, tipo, prioridad, idResponsable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        solicitudService.cambiarEstado(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Void> cerrar(
            @PathVariable UUID id,
            @Valid @RequestBody CerrarSolicitudRequest request) {
        solicitudService.cerrar(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialSolicitudResponse>> obtenerHistorial(
            @PathVariable UUID id) {
        return ResponseEntity.ok(solicitudService.obtenerHistorial(id));
    }
}
