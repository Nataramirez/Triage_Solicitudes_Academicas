package com.uniquindio.triage_academy.service.impl;

import com.uniquindio.triage_academy.dto.request.RegistrarAdministrativoRequest;
import com.uniquindio.triage_academy.dto.response.UsuarioResponse;
import com.uniquindio.triage_academy.helpers.BcryptPasswordHasher;
import com.uniquindio.triage_academy.helpers.exception.CustomException;
import com.uniquindio.triage_academy.helpers.mappers.HelperMappers;
import com.uniquindio.triage_academy.model.entity.Usuario;
import com.uniquindio.triage_academy.model.enums.RolUsuario;
import com.uniquindio.triage_academy.repository.UsuarioRepository;
import com.uniquindio.triage_academy.service.UsuarioInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioService implements UsuarioInterface {

    private final UsuarioRepository usuarioRepository;
    private final BcryptPasswordHasher passwordHasher;

    @Override
    public UsuarioResponse registrarAdministrativo(RegistrarAdministrativoRequest request) throws CustomException {

        log.info("@registrarAdministrativo SERV > Inicia registro del administrativo con request {}", request);

        validarCorreoRegistro(request.getCorreo());
        validarIdentificacionRegistro(request.getIdentificacion());

        Usuario administrativo = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .identificacion(request.getIdentificacion())
                .activo(true)
                .rol(RolUsuario.ADMINISTRATIVO)
                .contrasena(passwordHasher.hash(request.getContrasena()))
                .build();

        usuarioRepository.save(administrativo);

        log.info("@registrarAdministrativo SERV > Finaliza registro del administrativo con request {}", request);

        return HelperMappers.toUsuarioResponse(administrativo);
    }

    private void validarIdentificacionRegistro(String identificacion) throws CustomException {

        Optional<Usuario> usuarioIdentificacion = usuarioRepository.findByIdentificacion(identificacion);

        if (usuarioIdentificacion.isPresent()) {

            log.error("@validarIdentificacionRegistro SERV > La identificacion: {} enviada para el registro ya se " +
                    "encuentra registrada", identificacion);

            throw new CustomException(400, "El número de identificación proporcionado ya se encuentra registrado", null);
        }
    }

    private void validarCorreoRegistro(String correo) throws CustomException {

        Optional<Usuario> usuarioCorreo = usuarioRepository.findByCorreo(correo);

        if (usuarioCorreo.isPresent()) {

            log.error("@validarCorreoRegistro SERV > El correo electronico: {} enviado para el registro ya se " +
                    "encuentra registrado", correo);

            throw new CustomException(400, "El correo electrónico proporcionado ya se encuentra registrado", null);
        }
    }
}
