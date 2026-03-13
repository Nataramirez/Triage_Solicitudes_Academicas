package com.uniquindio.triage_academy.model.entity;

import com.uniquindio.triage_academy.model.enums.EstadoSolicitud;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_solicitud")
public class HistorialSolicitud {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud accion;

    @Column(nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id")
    private Solicitud solicitud;
}
