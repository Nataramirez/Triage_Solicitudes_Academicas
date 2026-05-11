package com.uniquindio.triage_academy.service;

import com.uniquindio.triage_academy.dto.request.RegistrarAdministrativoRequest;
import com.uniquindio.triage_academy.dto.response.UsuarioResponse;
import com.uniquindio.triage_academy.helpers.exception.CustomException;

public interface UsuarioInterface {

    UsuarioResponse registrarAdministrativo(RegistrarAdministrativoRequest request) throws CustomException;

}
