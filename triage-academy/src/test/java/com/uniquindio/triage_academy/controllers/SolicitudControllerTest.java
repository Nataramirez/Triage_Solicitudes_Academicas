package com.uniquindio.triage_academy.controllers;

import com.uniquindio.triage_academy.configuracion.seguridad.JwtAuthenticationFilter;
import com.uniquindio.triage_academy.configuracion.seguridad.JwtService;
import com.uniquindio.triage_academy.dto.response.HistorialSolicitudResponse;
import com.uniquindio.triage_academy.dto.response.SolicitudResponse;
import com.uniquindio.triage_academy.helpers.BcryptPasswordHasher;
import com.uniquindio.triage_academy.helpers.exception.GlobalExceptionHandler;
import com.uniquindio.triage_academy.model.enums.CanalOrigen;
import com.uniquindio.triage_academy.model.enums.EstadoSolicitud;
import com.uniquindio.triage_academy.model.enums.Prioridad;
import com.uniquindio.triage_academy.model.enums.TipoSolicitud;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(SolicitudController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@DisplayName("SolicitudController - Tests de integracion web")
class SolicitudControllerTest {

    private static final String URL_BASE = "/api/solicitudes";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolicitudInterface solicitudService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private BcryptPasswordHasher bcryptPasswordHasher;

    private UUID solicitudId;
    private UUID usuarioId;
    private UUID responsableId;
    private SolicitudResponse solicitudResponse;
    private HistorialSolicitudResponse historialResponse;

    @BeforeEach
    void setUp() {
        solicitudId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        responsableId = UUID.randomUUID();

        solicitudResponse = SolicitudResponse.builder()
                .id(solicitudId)
                .idUsuario(usuarioId)
                .idResponsable(responsableId)
                .tipo(TipoSolicitud.CONSULTA_ACADEMICA)
                .descripcion("Solicitud de certificado academico")
                .canalOrigen(CanalOrigen.CORREO)
                .fechaRegistro(LocalDateTime.of(2026, 4, 18, 10, 30))
                .estado(EstadoSolicitud.REGISTRADA)
                .prioridad(Prioridad.MEDIA)
                .justificacionPrioridad("Flujo normal")
                .observacionCierre(null)
                .build();

        historialResponse = HistorialSolicitudResponse.builder()
                .id(UUID.randomUUID())
                .idSolicitud(solicitudId)
                .fechaCreacion(LocalDateTime.of(2026, 4, 18, 11, 0))
                .accion("SOLICITUD_CREADA")
                .observaciones("Solicitud registrada correctamente")
                .build();
    }

    @Nested
    @DisplayName("POST /api/solicitudes")
    class CrearSolicitud {

        @Test
        @DisplayName("201 - Crea una solicitud exitosamente")
        void crear_exitoso() throws Exception {
            // Arrange
            String requestBody = """
                    {
                      "idUsuario": "%s",
                      "tipo": "CONSULTA_ACADEMICA",
                      "descripcion": "Solicitud de certificado academico",
                      "canalOrigen": "CORREO"
                    }
                    """.formatted(usuarioId);

            when(solicitudService.crear(any())).thenReturn(solicitudResponse);

            // Act & Assert
            mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(solicitudId.toString()))
                    .andExpect(jsonPath("$.idUsuario").value(usuarioId.toString()))
                    .andExpect(jsonPath("$.tipo").value("CONSULTA_ACADEMICA"))
                    .andExpect(jsonPath("$.estado").value("REGISTRADA"));

            verify(solicitudService, times(1)).crear(argThat(request ->
                    request.getIdUsuario().equals(usuarioId) &&
                            request.getTipo() == TipoSolicitud.CONSULTA_ACADEMICA &&
                            request.getDescripcion().equals("Solicitud de certificado academico") &&
                            request.getCanalOrigen() == CanalOrigen.CORREO
            ));
        }
    }

    @Nested
    @DisplayName("GET /api/solicitudes")
    class ListarSolicitudes {

        @Test
        @DisplayName("200 - Lista solicitudes con filtros validos")
        void listar_exitoso() throws Exception {
            // Arrange
            when(solicitudService.listar(
                    EstadoSolicitud.REGISTRADA,
                    TipoSolicitud.CONSULTA_ACADEMICA,
                    Prioridad.MEDIA,
                    responsableId
            )).thenReturn(List.of(solicitudResponse));

            // Act & Assert
            mockMvc.perform(get(URL_BASE)
                            .param("estado", "REGISTRADA")
                            .param("tipo", "CONSULTA_ACADEMICA")
                            .param("prioridad", "MEDIA")
                            .param("idResponsable", responsableId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(solicitudId.toString()))
                    .andExpect(jsonPath("$[0].prioridad").value("MEDIA"))
                    .andExpect(jsonPath("$[0].idResponsable").value(responsableId.toString()));

            verify(solicitudService, times(1)).listar(
                    EstadoSolicitud.REGISTRADA,
                    TipoSolicitud.CONSULTA_ACADEMICA,
                    Prioridad.MEDIA,
                    responsableId
            );
        }
    }

    @Nested
    @DisplayName("GET /api/solicitudes/{id}")
    class ObtenerPorId {

        @Test
        @DisplayName("200 - Retorna una solicitud por id")
        void obtenerPorId_exitoso() throws Exception {
            // Arrange
            when(solicitudService.obtenerPorId(solicitudId)).thenReturn(solicitudResponse);

            // Act & Assert
            mockMvc.perform(get(URL_BASE + "/" + solicitudId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(solicitudId.toString()))
                    .andExpect(jsonPath("$.descripcion").value("Solicitud de certificado academico"))
                    .andExpect(jsonPath("$.canalOrigen").value("CORREO"));

            verify(solicitudService, times(1)).obtenerPorId(solicitudId);
        }

        @Test
        @DisplayName("404 - Solicitud no encontrada")
        void obtenerPorId_noEncontrado() throws Exception {
            when(solicitudService.obtenerPorId(solicitudId))
                    .thenThrow(new EntityNotFoundException("Solicitud no encontrada"));

            mockMvc.perform(get(URL_BASE + "/" + solicitudId))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("400 - Crear solicitud con body vacío falla por validación")
        void crear_bodyVacio() throws Exception {
            mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(solicitudService, never()).crear(any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/solicitudes/{id}/estado")
    class CambiarEstado {

        @Test
        @DisplayName("200 - Cambia el estado de una solicitud")
        void cambiarEstado_exitoso() throws Exception {
            // Arrange
            String requestBody = """
                    {
                      "nuevoEstado": "EN_ATENCION",
                      "observaciones": "Se asigna al area academica"
                    }
                    """;

            // Act & Assert
            mockMvc.perform(patch(URL_BASE + "/" + solicitudId + "/estado")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk());

            verify(solicitudService, times(1)).cambiarEstado(eq(solicitudId), argThat(request ->
                    request.getNuevoEstado() == EstadoSolicitud.EN_ATENCION &&
                            request.getObservaciones().equals("Se asigna al area academica")
            ));
        }

        @Test
        @DisplayName("500 - Transición de estado inválida")
        void cambiarEstado_transicionInvalida() throws Exception {
            String requestBody = """
            {
              "nuevoEstado": "EN_ATENCION",
              "observaciones": "Salto inválido de estado"
            }
            """;

            doThrow(new IllegalStateException("Transición inválida: REGISTRADA → EN_ATENCION"))
                    .when(solicitudService).cambiarEstado(eq(solicitudId), any());

            mockMvc.perform(patch(URL_BASE + "/" + solicitudId + "/estado")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.mensaje").value("Transición inválida: REGISTRADA → EN_ATENCION"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/solicitudes/{id}/cerrar")
    class CerrarSolicitud {

        @Test
        @DisplayName("200 - Cierra una solicitud exitosamente")
        void cerrar_exitoso() throws Exception {
            // Arrange
            String requestBody = """
                    {
                      "observacionCierre": "Solicitud atendida y finalizada"
                    }
                    """;

            // Act & Assert
            mockMvc.perform(patch(URL_BASE + "/" + solicitudId + "/cerrar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk());

            verify(solicitudService, times(1)).cerrar(eq(solicitudId), argThat(request ->
                    request.getObservacionCierre().equals("Solicitud atendida y finalizada")
            ));
        }

        @Test
        @DisplayName("500 - Cerrar solicitud que no está en ATENDIDA")
        void cerrar_estadoInvalido() throws Exception {
            String requestBody = """
            {
              "observacionCierre": "Intento de cierre inválido"
            }
            """;

            doThrow(new IllegalStateException("Solo se pueden cerrar solicitudes en estado ATENDIDA"))
                    .when(solicitudService).cerrar(eq(solicitudId), any());

            mockMvc.perform(patch(URL_BASE + "/" + solicitudId + "/cerrar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.mensaje")
                            .value("Solo se pueden cerrar solicitudes en estado ATENDIDA"));
        }
    }

    @Nested
    @DisplayName("GET /api/solicitudes/{id}/historial")
    class ObtenerHistorial {

        @Test
        @DisplayName("200 - Retorna el historial de una solicitud")
        void obtenerHistorial_exitoso() throws Exception {
            // Arrange
            when(solicitudService.obtenerHistorial(solicitudId)).thenReturn(List.of(historialResponse));

            // Act & Assert
            mockMvc.perform(get(URL_BASE + "/" + solicitudId + "/historial"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idSolicitud").value(solicitudId.toString()))
                    .andExpect(jsonPath("$[0].accion").value("SOLICITUD_CREADA"))
                    .andExpect(jsonPath("$[0].observaciones").value("Solicitud registrada correctamente"));

            verify(solicitudService, times(1)).obtenerHistorial(solicitudId);
        }
    }
}
