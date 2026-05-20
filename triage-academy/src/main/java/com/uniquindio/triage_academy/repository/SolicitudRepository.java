package com.uniquindio.triage_academy.repository;

import com.uniquindio.triage_academy.model.entity.Solicitud;
import com.uniquindio.triage_academy.model.enums.EstadoSolicitud;
import com.uniquindio.triage_academy.model.enums.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID>, JpaSpecificationExecutor<Solicitud> {
    List<Solicitud> findByUsuarioId(UUID usuarioId);

    long countByResponsable_IdAndPrioridadAndEstadoNot(UUID responsableId, Prioridad prioridad, EstadoSolicitud estado);
}
