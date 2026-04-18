package com.uniquindio.triage_academy.service.impl;

import com.uniquindio.triage_academy.configuracion.seguridad.JwtService;
import com.uniquindio.triage_academy.dto.request.LoginRequest;
import com.uniquindio.triage_academy.dto.request.RegistrarUsuarioRequest;
import com.uniquindio.triage_academy.dto.response.AuthResponse;
import com.uniquindio.triage_academy.helpers.BcryptPasswordHasher;
import com.uniquindio.triage_academy.helpers.exception.CustomException;
import com.uniquindio.triage_academy.helpers.mappers.HelperMappers;
import com.uniquindio.triage_academy.model.entity.Usuario;
import com.uniquindio.triage_academy.repository.UsuarioRepository;
import com.uniquindio.triage_academy.service.AuthInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements AuthInterface {

    @Value("${jwt.expiration}")
    private long expiration;

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final BcryptPasswordHasher passwordHasher;

    @Override
    public AuthResponse registrarUsuario(RegistrarUsuarioRequest request) throws CustomException {

        log.info("@registrarUsuario SERV > Inicia registro del usuario con request {}", request);

        validarCorreoRegistro(request.getCorreo());
        validarIdentificacionRegistro(request.getIdentificacion());
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .identificacion(request.getIdentificacion())
                .activo(true)
                .rol(request.getRol())
                .contrasena(passwordHasher.hash(request.getContrasena()))
                .build();

        usuarioRepository.save(usuario);

        log.info("@registrarUsuario SERV > Finaliza registro del usuario con request {}", request);

        return generarRespuestaAutorizacion(usuario);
    }

    @Override
    public AuthResponse iniciarSesion(LoginRequest request) throws CustomException {

        Optional<Usuario> optionalUsuario = usuarioRepository.findByCorreo(request.getCorreo());
        Usuario usuario = optionalUsuario.orElseThrow(() ->
                new CustomException(401, "Credenciales no válidas", null));

        if (!passwordHasher.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new CustomException(400, "Credenciales no válidas", null);
        }

        return generarRespuestaAutorizacion(usuario);
    }

    private AuthResponse generarRespuestaAutorizacion(Usuario usuario) {

        String token = jwtService.obtenerToken(usuario);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiration)
                .usuario(HelperMappers.toUsuarioResponse(usuario))
                .build();
    }

    private void validarIdentificacionRegistro(String identificacion) throws CustomException {

        Optional<Usuario> usuarioIdentificacion = usuarioRepository.findByIdentificacion(identificacion);

        if (usuarioIdentificacion.isPresent()) {

            log.error("@validarIdentificacionRegistro SERV > La identificacion: {} enviada para el regisro ya se " +
                    "encuentra registrada", identificacion);

            throw new CustomException(400, "El número de identificación proporcionado ya se encuentra registrado", null);
        }
    }

    private void validarCorreoRegistro(String correo) throws CustomException {

        Optional<Usuario> usuarioCorreo = usuarioRepository.findByCorreo(correo);

        if (usuarioCorreo.isPresent()) {

            log.error("@validarCorreoRegistro SERV > El correo electronico: {} enviado para el regisro ya se " +
                    "encuentra registrado", correo);

            throw new CustomException(400, "El correo electrónico proporcionado ya se encuentra registrado", null);
        }
    }
}
