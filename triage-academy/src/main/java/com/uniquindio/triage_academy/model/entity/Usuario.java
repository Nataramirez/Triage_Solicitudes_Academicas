package com.uniquindio.triage_academy.model.entity;

import com.uniquindio.triage_academy.model.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 20, unique = true)
    private String identificacion;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RolUsuario rol;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private String contrasena;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Solicitud> listaSolicitudes = new ArrayList<>();

    @OneToMany(mappedBy = "responsable", fetch = FetchType.LAZY)
    private List<Solicitud> listaSolicitudesAsignadas = new ArrayList<>();
}
