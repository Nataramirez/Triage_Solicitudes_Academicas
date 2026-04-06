package com.uniquindio.triage_academy.repository;

import com.uniquindio.triage_academy.model.entity.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface HistorialRepository extends JpaRepository<HistorialSolicitud, UUID> {
}
