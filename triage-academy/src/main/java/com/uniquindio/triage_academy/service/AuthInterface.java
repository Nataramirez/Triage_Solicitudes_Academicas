package com.uniquindio.triage_academy.service;

import com.uniquindio.triage_academy.dto.request.LoginRequest;
import com.uniquindio.triage_academy.dto.request.RegistrarUsuarioRequest;
import com.uniquindio.triage_academy.dto.response.AuthResponse;
import com.uniquindio.triage_academy.helpers.exception.CustomException;

public interface AuthInterface {

    AuthResponse registrarUsuario(RegistrarUsuarioRequest request) throws CustomException;

    AuthResponse iniciarSesion(LoginRequest request) throws CustomException;

}
