package com.uniquindio.triage_academy.repository;

import com.uniquindio.triage_academy.model.entity.Usuario;
import com.uniquindio.triage_academy.model.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByIdentificacion(String identificacion);

    List<Usuario> findAllByRol(RolUsuario rol);

    List<Usuario> findAllByRolAndIdentificacionStartingWith(RolUsuario rol, String identificacion);

}
