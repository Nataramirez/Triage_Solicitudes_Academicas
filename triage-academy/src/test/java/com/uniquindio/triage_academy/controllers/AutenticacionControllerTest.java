package com.uniquindio.triage_academy.controllers;

import com.uniquindio.triage_academy.configuracion.seguridad.JwtAuthenticationFilter;
import com.uniquindio.triage_academy.configuracion.seguridad.JwtService;
import com.uniquindio.triage_academy.dto.request.LoginRequest;
import com.uniquindio.triage_academy.dto.request.RegistrarUsuarioRequest;
import com.uniquindio.triage_academy.dto.response.AuthResponse;
import com.uniquindio.triage_academy.dto.response.UsuarioResponse;
import com.uniquindio.triage_academy.helpers.BcryptPasswordHasher;
import com.uniquindio.triage_academy.helpers.exception.CustomException;
import com.uniquindio.triage_academy.helpers.exception.GlobalExceptionHandler;
import com.uniquindio.triage_academy.model.enums.RolUsuario;
import com.uniquindio.triage_academy.repository.UsuarioRepository;
import com.uniquindio.triage_academy.service.AuthInterface;
import com.uniquindio.triage_academy.service.impl.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutenticacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@DisplayName("AutenticacionController - Tests de integración web")
class AutenticacionControllerTest {

    private static final String URL_REGISTRO       = "/api/autenticacion/registro";
    private static final String URL_INICIAR_SESION = "/api/autenticacion/iniciar-sesion";

    private static final String CORREO_VALIDO     = "ana@uniquindio.edu.co";
    private static final String CONTRASENA_VALIDA = "Secreta123*";
    private static final String CORREO_INVALIDO   = "correo-no-valido";

    // Códigos HTTP como int — alineados con CustomException(int statusCode, String mensaje, Throwable cause)
    private static final int HTTP_CONFLICT     = 409;
    private static final int HTTP_UNAUTHORIZED = 401;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthInterface authInterface;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private BcryptPasswordHasher bcryptPasswordHasher;

    private AuthResponse mockAuthResponse;

    @BeforeEach
    void setUp() {
        mockAuthResponse = AuthResponse.builder()
                .accessToken("mock.jwt.token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .usuario(
                        UsuarioResponse.builder()
                                .id(UUID.randomUUID())
                                .identificacion("12345678")
                                .nombre("Ana Torres")
                                .correo(CORREO_VALIDO)
                                .rol(RolUsuario.ESTUDIANTE)
                                .activo(true)
                                .build()
                )
                .build();
    }

    @Nested
    @DisplayName("POST /registro")
    class Registro {

        @Test
        @DisplayName("201 - Registro exitoso con datos válidos")
        void registrarUsuario_exitoso() throws Exception {
            RegistrarUsuarioRequest request = new RegistrarUsuarioRequest();
            request.setIdentificacion("12345678");
            request.setNombre("Ana Torres");
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena(CONTRASENA_VALIDA);
            request.setRol(RolUsuario.ESTUDIANTE);

            when(authInterface.registrarUsuario(any())).thenReturn(mockAuthResponse);

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                    .andExpect(jsonPath("$.usuario.correo").value(CORREO_VALIDO))
                    .andExpect(jsonPath("$.usuario.rol").value("ESTUDIANTE"));
        }

        @Test
        @DisplayName("400 - Correo con formato inválido")
        void registrarUsuario_correoInvalido() throws Exception {
            String json = buildRegistroJson("12345678", "Ana Torres", CORREO_INVALIDO, CONTRASENA_VALIDA, "ESTUDIANTE");

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.correo").value("El correo electrónico proporcionado no es válido"));
        }

        @Test
        @DisplayName("400 - Correo vacío")
        void registrarUsuario_correoVacio() throws Exception {
            String json = buildRegistroJson("12345678", "Ana Torres", "", CONTRASENA_VALIDA, "ESTUDIANTE");

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.correo").exists());
        }

        @Test
        @DisplayName("400 - Identificación vacía")
        void registrarUsuario_identificacionVacia() throws Exception {
            String json = buildRegistroJson("", "Ana Torres", CORREO_VALIDO, CONTRASENA_VALIDA, "ESTUDIANTE");

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.identificacion").value("La identificación del usuario es requerida"));
        }

        @Test
        @DisplayName("400 - Nombre vacío")
        void registrarUsuario_nombreVacio() throws Exception {
            String json = buildRegistroJson("12345678", "", CORREO_VALIDO, CONTRASENA_VALIDA, "ESTUDIANTE");

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.nombre").value("El nombre del usuario es requerido"));
        }

        @Test
        @DisplayName("400 - Contraseña vacía")
        void registrarUsuario_contrasenaVacia() throws Exception {
            String json = buildRegistroJson("12345678", "Ana Torres", CORREO_VALIDO, "", "ESTUDIANTE");

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.contrasena").value("La contraseña es requerida"));
        }

        @Test
        @DisplayName("400 - Rol ausente")
        void registrarUsuario_rolAusente() throws Exception {
            String json = """
                    {
                      "identificacion": "12345678",
                      "nombre": "Ana Torres",
                      "correo": "ana@uniquindio.edu.co",
                      "contrasena": "Secreta123*"
                    }
                    """;

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.rol").value("El rol es requerido"));
        }

        @Test
        @DisplayName("409 - El servicio lanza CustomException (usuario ya existe)")
        void registrarUsuario_usuarioYaExiste() throws Exception {
            RegistrarUsuarioRequest request = new RegistrarUsuarioRequest();
            request.setIdentificacion("12345678");
            request.setNombre("Ana Torres");
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena(CONTRASENA_VALIDA);
            request.setRol(RolUsuario.ESTUDIANTE);

            when(authInterface.registrarUsuario(any()))
                    .thenThrow(new CustomException(HTTP_CONFLICT, "El usuario ya se encuentra registrado", null));

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.mensaje").value("El usuario ya se encuentra registrado"));
        }

        @Test
        @DisplayName("500 - El servicio lanza una excepción inesperada")
        void registrarUsuario_errorInesperado() throws Exception {
            RegistrarUsuarioRequest request = new RegistrarUsuarioRequest();
            request.setIdentificacion("12345678");
            request.setNombre("Ana Torres");
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena(CONTRASENA_VALIDA);
            request.setRol(RolUsuario.ESTUDIANTE);

            when(authInterface.registrarUsuario(any()))
                    .thenThrow(new RuntimeException("Error interno inesperado"));

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Internal server error"));
        }
    }

    @Nested
    @DisplayName("POST /iniciar-sesion")
    class IniciarSesion {

        @Test
        @DisplayName("200 - Inicio de sesión exitoso con credenciales válidas")
        void iniciarSesion_exitoso() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena(CONTRASENA_VALIDA);

            when(authInterface.iniciarSesion(any())).thenReturn(mockAuthResponse);

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.usuario.correo").value(CORREO_VALIDO));
        }

        @Test
        @DisplayName("400 - Correo con formato inválido")
        void iniciarSesion_correoInvalido() throws Exception {
            String json = buildLoginJson(CORREO_INVALIDO, CONTRASENA_VALIDA);

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.correo").exists());
        }

        @Test
        @DisplayName("400 - Contraseña vacía")
        void iniciarSesion_contrasenaVacia() throws Exception {
            String json = buildLoginJson(CORREO_VALIDO, "");

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.contrasena").exists());
        }

        @Test
        @DisplayName("400 - Correo vacío")
        void iniciarSesion_correoVacio() throws Exception {
            String json = buildLoginJson("", CONTRASENA_VALIDA);

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.correo").exists());
        }

        @Test
        @DisplayName("401 - Credenciales incorrectas lanzan CustomException")
        void iniciarSesion_credencialesIncorrectas() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena("ClaveIncorrecta1*");

            when(authInterface.iniciarSesion(any()))
                    .thenThrow(new CustomException(HTTP_UNAUTHORIZED, "Credenciales incorrectas", null));

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"));
        }

        @Test
        @DisplayName("500 - El servicio lanza una excepción inesperada")
        void iniciarSesion_errorInesperado() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena(CONTRASENA_VALIDA);

            when(authInterface.iniciarSesion(any()))
                    .thenThrow(new RuntimeException("Error interno inesperado"));

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Internal server error"));
        }
    }

    private String buildRegistroJson(String identificacion, String nombre, String correo,
                                     String contrasena, String rol) {
        return String.format("""
                {
                  "identificacion": "%s",
                  "nombre": "%s",
                  "correo": "%s",
                  "contrasena": "%s",
                  "rol": "%s"
                }
                """, identificacion, nombre, correo, contrasena, rol);
    }

    private String buildLoginJson(String correo, String contrasena) {
        return String.format("""
                {
                  "correo": "%s",
                  "contrasena": "%s"
                }
                """, correo, contrasena);
    }
}