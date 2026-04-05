package com.uniquindio.triage_academy.repository;

import com.uniquindio.triage_academy.model.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID> {
}
