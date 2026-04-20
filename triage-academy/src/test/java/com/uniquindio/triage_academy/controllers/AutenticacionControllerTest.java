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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.never;

@WebMvcTest(AutenticacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@DisplayName("AutenticacionController - Tests de integración web")
class AutenticacionControllerTest {

    private static final String URL_REGISTRO = "/api/autenticacion/registro";
    private static final String URL_INICIAR_SESION = "/api/autenticacion/iniciar-sesion";

    private static final String CORREO_VALIDO = "ana@uniquindio.edu.co";
    private static final String CONTRASENA_VALIDA = "Secreta123*";

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
            // Arrange
            RegistrarUsuarioRequest request = new RegistrarUsuarioRequest();
            request.setIdentificacion("12345678");
            request.setNombre("Ana Torres");
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena(CONTRASENA_VALIDA);
            request.setRol(RolUsuario.ESTUDIANTE);

            when(authInterface.registrarUsuario(any())).thenReturn(mockAuthResponse);

            // Act & Assert
            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                    .andExpect(jsonPath("$.usuario.correo").value(CORREO_VALIDO))
                    .andExpect(jsonPath("$.usuario.rol").value("ESTUDIANTE"));

            verify(authInterface, times(1)).registrarUsuario(argThat(registro ->
                    registro.getIdentificacion().equals(request.getIdentificacion()) &&
                            registro.getNombre().equals(request.getNombre()) &&
                            registro.getCorreo().equals(request.getCorreo()) &&
                            registro.getContrasena().equals(request.getContrasena()) &&
                            registro.getRol() == request.getRol()
            ));
        }

        @Test
        @DisplayName("400 - Falla si el correo ya existe")
        void registrar_correoYaExiste() throws Exception {
            String requestBody = """
            {
              "identificacion": "12345678",
              "nombre": "Ana Torres",
              "correo": "ana@uniquindio.edu.co",
              "contrasena": "Secreta123*",
              "rol": "ESTUDIANTE"
            }
            """;

            when(authInterface.registrarUsuario(any()))
                    .thenThrow(new CustomException(400,
                            "El correo electrónico proporcionado ya se encuentra registrado", null));

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensaje")
                            .value("El correo electrónico proporcionado ya se encuentra registrado"));
        }

        @Test
        @DisplayName("400 - Falla si la identificación ya existe")
        void registrar_identificacionYaExiste() throws Exception {
            String requestBody = """
            {
              "identificacion": "12345678",
              "nombre": "Ana Torres",
              "correo": "ana@uniquindio.edu.co",
              "contrasena": "Secreta123*",
              "rol": "ESTUDIANTE"
            }
            """;

            when(authInterface.registrarUsuario(any()))
                    .thenThrow(new CustomException(400,
                            "El número de identificación proporcionado ya se encuentra registrado", null));

            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensaje")
                            .value("El número de identificación proporcionado ya se encuentra registrado"));
        }

        @Test
        @DisplayName("400 - Falla si el body está vacío")
        void registrar_bodyVacio() throws Exception {
            mockMvc.perform(post(URL_REGISTRO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(authInterface, never()).registrarUsuario(any());
        }
    }

    @Nested
    @DisplayName("POST /iniciar-sesion")
    class IniciarSesion {

        @Test
        @DisplayName("200 - Inicio de sesión exitoso con credenciales válidas")
        void iniciarSesion_exitoso() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setCorreo(CORREO_VALIDO);
            request.setContrasena(CONTRASENA_VALIDA);

            when(authInterface.iniciarSesion(any())).thenReturn(mockAuthResponse);

            // Act & Assert
            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.usuario.correo").value(CORREO_VALIDO));
            verify(authInterface, times(1)).iniciarSesion(argThat(login ->
                    login.getCorreo().equals(request.getCorreo()) &&
                            login.getContrasena().equals(request.getContrasena())
            ));
        }

        @Test
        @DisplayName("401 - Falla si el usuario no existe")
        void iniciarSesion_usuarioNoExiste() throws Exception {
            String requestBody = """
                    {
                      "correo": "noexiste@uniquindio.edu.co",
                      "contrasena": "Secreta123*"
                    }
                    """;

            when(authInterface.iniciarSesion(any()))
                    .thenThrow(new CustomException(401, "Credenciales no válidas", null));

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.mensaje").value("Credenciales no válidas"));
        }

        @Test
        @DisplayName("400 - Falla si la contraseña es incorrecta")
        void iniciarSesion_contrasenaIncorrecta() throws Exception {
            String requestBody = """
                    {
                      "correo": "ana@uniquindio.edu.co",
                      "contrasena": "MalaClave99!"
                    }
                    """;

            when(authInterface.iniciarSesion(any()))
                    .thenThrow(new CustomException(400, "Credenciales no válidas", null));

            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensaje").value("Credenciales no válidas"));
        }

        @Test
        @DisplayName("400 - Falla si el body está vacío")
        void iniciarSesion_bodyVacio() throws Exception {
            mockMvc.perform(post(URL_INICIAR_SESION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(authInterface, never()).iniciarSesion(any());
        }
    }
}
