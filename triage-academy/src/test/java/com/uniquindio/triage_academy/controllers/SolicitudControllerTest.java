package com.uniquindio.triage_academy.controllers;

import com.uniquindio.triage_academy.configuracion.seguridad.JwtAuthenticationFilter;
import com.uniquindio.triage_academy.configuracion.seguridad.JwtService;
import com.uniquindio.triage_academy.dto.request.CambiarEstadoRequest;
import com.uniquindio.triage_academy.dto.request.CerrarSolicitudRequest;
import com.uniquindio.triage_academy.dto.request.CrearSolicitudRequest;
import com.uniquindio.triage_academy.dto.response.HistorialSolicitudResponse;
import com.uniquindio.triage_academy.dto.response.SolicitudResponse;
import com.uniquindio.triage_academy.helpers.BcryptPasswordHasher;
import com.uniquindio.triage_academy.helpers.exception.CustomException;
import com.uniquindio.triage_academy.helpers.exception.GlobalExceptionHandler;
import com.uniquindio.triage_academy.model.enums.*;
import com.uniquindio.triage_academy.repository.UsuarioRepository;
import com.uniquindio.triage_academy.service.SolicitudInterface;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitudController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@DisplayName("SolicitudController - Tests de integración web")
class SolicitudControllerTest {

    private static final String URL_BASE      = "/api/solicitudes";
    private static final UUID   ID_SOLICITUD  = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
    private static final UUID   ID_USUARIO    = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SolicitudInterface solicitudInterface;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private BcryptPasswordHasher bcryptPasswordHasher;

    private SolicitudResponse mockSolicitudResponse;
    private HistorialSolicitudResponse mockHistorialResponse;

    @BeforeEach
    void setUp() {
        mockSolicitudResponse = SolicitudResponse.builder()
                .id(ID_SOLICITUD)
                .idUsuario(ID_USUARIO)
                .tipo(TipoSolicitud.ACADEMICA)
                .descripcion("Solicitud de prueba")
                .canalOrigen(CanalOrigen.WEB)
                .fechaRegistro(LocalDateTime.now())
                .estado(EstadoSolicitud.ABIERTA)
                .prioridad(Prioridad.MEDIA)
                .build();

        mockHistorialResponse = HistorialSolicitudResponse.builder()
                .id(UUID.randomUUID())
                .idSolicitud(ID_SOLICITUD)
                .fechaCreacion(LocalDateTime.now())
                .accion("CREACION")
                .observaciones("Solicitud creada")
                .build();
    }

    @Nested
    @DisplayName("GET /solicitudes")
    class ObtenerSolicitudes {

        @Test
        @DisplayName("200 - Retorna lista de solicitudes")
        void listar_exitoso() throws Exception {
            when(solicitudInterface.listar(any(), any(), any(), any()))
                    .thenReturn(List.of(mockSolicitudResponse));

            mockMvc.perform(get(URL_BASE))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("200 - Retorna lista vacía cuando no hay solicitudes")
        void listar_vacio() throws Exception {
            when(solicitudInterface.listar(any(), any(), any(), any()))
                    .thenReturn(List.of());

            mockMvc.perform(get(URL_BASE))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /solicitudes/{id}")
    class ObtenerPorId {

        @Test
        @DisplayName("200 - Retorna solicitud existente por ID")
        void obtenerPorId_exitoso() throws Exception {
            when(solicitudInterface.obtenerPorId(ID_SOLICITUD))
                    .thenReturn(mockSolicitudResponse);

            mockMvc.perform(get(URL_BASE + "/{id}", ID_SOLICITUD))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ID_SOLICITUD.toString()))
                    .andExpect(jsonPath("$.estado").value("ABIERTA"));
        }

        @Test
        @DisplayName("404 - Solicitud no encontrada lanza CustomException")
        void obtenerPorId_noEncontrada() throws Exception {
            when(solicitudInterface.obtenerPorId(ID_SOLICITUD))
                    .thenThrow(new CustomException(404, "Solicitud no encontrada", null));

            mockMvc.perform(get(URL_BASE + "/{id}", ID_SOLICITUD))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensaje").value("Solicitud no encontrada"));
        }
    }

    @Nested
    @DisplayName("POST /solicitudes")
    class CrearSolicitud {

        @Test
        @DisplayName("201 - Crea solicitud exitosamente")
        void crear_exitoso() throws Exception {
            when(solicitudInterface.crear(any())).thenReturn(mockSolicitudResponse);

            String json = """
                    {
                      "idUsuario": "%s",
                      "tipo": "ACADEMICA",
                      "descripcion": "Solicitud de prueba",
                      "canalOrigen": "WEB"
                    }
                    """.formatted(ID_USUARIO);

            mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tipo").value("ACADEMICA"));
        }

        @Test
        @DisplayName("400 - Falla si descripcion está vacía")
        void crear_descripcionVacia() throws Exception {
            String json = """
                    {
                      "idUsuario": "%s",
                      "tipo": "ACADEMICA",
                      "descripcion": "",
                      "canalOrigen": "WEB"
                    }
                    """.formatted(ID_USUARIO);

            mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 - Falla si tipo es nulo")
        void crear_tipoNulo() throws Exception {
            String json = """
                    {
                      "idUsuario": "%s",
                      "descripcion": "Solicitud de prueba",
                      "canalOrigen": "WEB"
                    }
                    """.formatted(ID_USUARIO);

            mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /solicitudes/{id}/estado")
    class CambiarEstado {

        @Test
        @DisplayName("204 - Cambia estado exitosamente")
        void cambiarEstado_exitoso() throws Exception {
            doNothing().when(solicitudInterface).cambiarEstado(eq(ID_SOLICITUD), any());

            String json = """
                    {
                      "nuevoEstado": "EN_PROCESO",
                      "observaciones": "Asignada al equipo"
                    }
                    """;

            mockMvc.perform(patch(URL_BASE + "/{id}/estado", ID_SOLICITUD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("400 - Falla si nuevoEstado es nulo")
        void cambiarEstado_estadoNulo() throws Exception {
            String json = """
                    {
                      "observaciones": "Sin estado"
                    }
                    """;

            mockMvc.perform(patch(URL_BASE + "/{id}/estado", ID_SOLICITUD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 - Solicitud no encontrada al cambiar estado")
        void cambiarEstado_noEncontrada() throws Exception {
            doThrow(new CustomException(404, "Solicitud no encontrada", null))
                    .when(solicitudInterface).cambiarEstado(eq(ID_SOLICITUD), any());

            String json = """
                    {
                      "nuevoEstado": "EN_PROCESO"
                    }
                    """;

            mockMvc.perform(patch(URL_BASE + "/{id}/estado", ID_SOLICITUD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensaje").value("Solicitud no encontrada"));
        }
    }

    @Nested
    @DisplayName("PATCH /solicitudes/{id}/cerrar")
    class CerrarSolicitud {

        @Test
        @DisplayName("204 - Cierra solicitud exitosamente")
        void cerrar_exitoso() throws Exception {
            doNothing().when(solicitudInterface).cerrar(eq(ID_SOLICITUD), any());

            String json = """
                    {
                      "observacionCierre": "Solicitud resuelta satisfactoriamente"
                    }
                    """;

            mockMvc.perform(patch(URL_BASE + "/{id}/cerrar", ID_SOLICITUD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("400 - Falla si observacionCierre está vacía")
        void cerrar_observacionVacia() throws Exception {
            String json = """
                    {
                      "observacionCierre": ""
                    }
                    """;

            mockMvc.perform(patch(URL_BASE + "/{id}/cerrar", ID_SOLICITUD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /solicitudes/{id}/historial")
    class ObtenerHistorial {

        @Test
        @DisplayName("200 - Retorna historial de una solicitud")
        void historial_exitoso() throws Exception {
            when(solicitudInterface.obtenerHistorial(ID_SOLICITUD))
                    .thenReturn(List.of(mockHistorialResponse));

            mockMvc.perform(get(URL_BASE + "/{id}/historial", ID_SOLICITUD))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idSolicitud").value(ID_SOLICITUD.toString()))
                    .andExpect(jsonPath("$[0].accion").value("CREACION"));
        }

        @Test
        @DisplayName("200 - Retorna lista vacía si no hay historial")
        void historial_vacio() throws Exception {
            when(solicitudInterface.obtenerHistorial(ID_SOLICITUD))
                    .thenReturn(List.of());

            mockMvc.perform(get(URL_BASE + "/{id}/historial", ID_SOLICITUD))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("404 - Solicitud no encontrada al consultar historial")
        void historial_noEncontrada() throws Exception {
            when(solicitudInterface.obtenerHistorial(ID_SOLICITUD))
                    .thenThrow(new CustomException(404, "Solicitud no encontrada", null));

            mockMvc.perform(get(URL_BASE + "/{id}/historial", ID_SOLICITUD))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensaje").value("Solicitud no encontrada"));
        }
    }
}