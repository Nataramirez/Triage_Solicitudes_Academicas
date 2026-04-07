package com.uniquindio.triage_academy.controllers;

import com.uniquindio.triage_academy.dto.request.LoginRequest;
import com.uniquindio.triage_academy.dto.request.RegistrarUsuarioRequest;
import com.uniquindio.triage_academy.dto.response.AuthResponse;
import com.uniquindio.triage_academy.helpers.exception.CustomException;
import com.uniquindio.triage_academy.service.AuthInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autenticacion")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AuthInterface authInterface;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registarUsuario(@RequestBody @Valid RegistrarUsuarioRequest request) throws CustomException {
        AuthResponse response = authInterface.registrarUsuario(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<AuthResponse> identificarUsuario(@RequestBody @Valid LoginRequest request) throws CustomException {
        AuthResponse response = authInterface.iniciarSesion(request);
        return ResponseEntity.status(200).body(response);
    }
}
