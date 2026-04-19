package com.uniquindio.triage_academy.service.impl;

import com.uniquindio.triage_academy.configuracion.seguridad.JwtService;
import com.uniquindio.triage_academy.dto.request.LoginRequest;
import com.uniquindio.triage_academy.dto.request.RegistrarUsuarioRequest;
import com.uniquindio.triage_academy.dto.response.AuthResponse;
import com.uniquindio.triage_academy.helpers.BcryptPasswordHasher;
import com.uniquindio.triage_academy.helpers.exception.CustomException;
import com.uniquindio.triage_academy.model.entity.Usuario;
import com.uniquindio.triage_academy.model.enums.RolUsuario;
import com.uniquindio.triage_academy.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Tests unitarios")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BcryptPasswordHasher passwordHasher;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "expiration", 3600L);

        usuarioMock = Usuario.builder()
                .id(UUID.randomUUID())
                .identificacion("12345678")
                .nombre("Ana Torres")
                .correo("ana@uniquindio.edu.co")
                .contrasena("$2a$hashed")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("registrarUsuario()")
    class RegistrarUsuario {

        @Test
        @DisplayName("Registra usuario exitosamente y retorna AuthResponse con token")
        void registrar_exitoso() throws CustomException {
            // Arrange
            RegistrarUsuarioRequest request = buildRequest();

            when(usuarioRepository.findByCorreo(request.getCorreo())).thenReturn(Optional.empty());
            when(usuarioRepository.findByIdentificacion(request.getIdentificacion())).thenReturn(Optional.empty());
            when(passwordHasher.hash(request.getContrasena())).thenReturn("$2a$hashed");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);
            when(jwtService.obtenerToken(any(Usuario.class))).thenReturn("jwt.token.mock");

            // Act
            AuthResponse response = authService.registrarUsuario(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("jwt.token.mock");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getUsuario().getCorreo()).isEqualTo(request.getCorreo());

            verify(usuarioRepository, times(1)).save(any(Usuario.class));
            verify(passwordHasher, times(1)).hash("Secreta123*");
        }

        private RegistrarUsuarioRequest buildRequest() {
            RegistrarUsuarioRequest r = new RegistrarUsuarioRequest();
            r.setIdentificacion("12345678");
            r.setNombre("Ana Torres");
            r.setCorreo("ana@uniquindio.edu.co");
            r.setContrasena("Secreta123*");
            r.setRol(RolUsuario.ESTUDIANTE);
            return r;
        }
    }

    @Nested
    @DisplayName("iniciarSesion()")
    class IniciarSesion {

        @Test
        @DisplayName("Retorna AuthResponse con token cuando las credenciales son correctas")
        void sesion_exitosa() throws CustomException {
            // Arrange
            LoginRequest request = buildLoginRequest("ana@uniquindio.edu.co", "Secreta123*");

            when(usuarioRepository.findByCorreo(request.getCorreo()))
                    .thenReturn(Optional.of(usuarioMock));
            when(passwordHasher.matches(request.getContrasena(), usuarioMock.getContrasena()))
                    .thenReturn(true);
            when(jwtService.obtenerToken(usuarioMock)).thenReturn("jwt.token.mock");

            // Act
            AuthResponse response = authService.iniciarSesion(request);

            // Assert
            assertThat(response.getAccessToken()).isEqualTo("jwt.token.mock");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getUsuario().getCorreo()).isEqualTo("ana@uniquindio.edu.co");

            verify(usuarioRepository, times(1)).findByCorreo(request.getCorreo());
            verify(passwordHasher, times(1)).matches(request.getContrasena(), usuarioMock.getContrasena());
            verify(jwtService, times(1)).obtenerToken(usuarioMock);
        }

        private LoginRequest buildLoginRequest(String correo, String contrasena) {
            LoginRequest r = new LoginRequest();
            r.setCorreo(correo);
            r.setContrasena(contrasena);
            return r;
        }
    }
}
