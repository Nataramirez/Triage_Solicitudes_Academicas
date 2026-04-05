package com.uniquindio.triage_academy.repository;

import com.uniquindio.triage_academy.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> encontrarPorCorreo(String correo);
    boolean validarCorreo(String correo);
}
