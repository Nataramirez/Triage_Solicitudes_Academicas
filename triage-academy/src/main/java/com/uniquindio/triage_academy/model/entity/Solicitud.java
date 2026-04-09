package com.uniquindio.triage_academy.model.entity;

import com.uniquindio.triage_academy.model.enums.CanalOrigen;
import com.uniquindio.triage_academy.model.enums.EstadoSolicitud;
import com.uniquindio.triage_academy.model.enums.Prioridad;
import com.uniquindio.triage_academy.model.enums.TipoSolicitud;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
public class Solicitud {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoSolicitud tipo;

    @Column(nullable = false, name = "canal_origen")
    @Enumerated(EnumType.STRING)
    private CanalOrigen canalOrigen;

    @Column(nullable = false, name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @Column(nullable = false, name = "descripcion")
    private String descripcion;

    @Column(name = "justificacion_prioridad")
    private String justificacionPrioridad;

    @Column(name = "observacion_cierre")
    private String observacionCierre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY)
    private List<HistorialSolicitud> listaHistorial = new ArrayList<>();
}
