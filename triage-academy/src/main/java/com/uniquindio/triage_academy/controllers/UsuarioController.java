package com.uniquindio.triage_academy.controllers;

import com.uniquindio.triage_academy.dto.request.RegistrarAdministrativoRequest;
import com.uniquindio.triage_academy.dto.response.UsuarioResponse;
import com.uniquindio.triage_academy.helpers.exception.CustomException;
import com.uniquindio.triage_academy.service.UsuarioInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioInterface usuarioService;

    @PostMapping("/administrativos")
    @PreAuthorize("hasAuthority('DIRECTOR')")
    public ResponseEntity<UsuarioResponse> registrarAdministrativo(
            @RequestBody @Valid RegistrarAdministrativoRequest request) throws CustomException {
        UsuarioResponse response = usuarioService.registrarAdministrativo(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/administrativos")
    @PreAuthorize("hasAuthority('DIRECTOR')")
    public ResponseEntity<List<UsuarioResponse>> listarAdministrativos() {
        return ResponseEntity.ok(usuarioService.listarAdministrativos());
    }

    @GetMapping("/estudiantes")
    @PreAuthorize("hasAuthority('ADMINISTRATIVO') or hasAuthority('DIRECTOR')")
    public ResponseEntity<List<UsuarioResponse>> buscarEstudiantes(
            @RequestParam(required = false, defaultValue = "") String cedula) {
        return ResponseEntity.ok(usuarioService.buscarEstudiantes(cedula));
    }
}
