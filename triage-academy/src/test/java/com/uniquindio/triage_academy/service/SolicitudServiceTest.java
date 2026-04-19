package com.uniquindio.triage_academy.service.impl;

import com.uniquindio.triage_academy.dto.request.CambiarEstadoRequest;
import com.uniquindio.triage_academy.dto.request.CerrarSolicitudRequest;
import com.uniquindio.triage_academy.dto.request.CrearSolicitudRequest;
import com.uniquindio.triage_academy.dto.response.HistorialSolicitudResponse;
import com.uniquindio.triage_academy.dto.response.SolicitudResponse;
import com.uniquindio.triage_academy.model.entity.HistorialSolicitud;
import com.uniquindio.triage_academy.model.entity.Solicitud;
import com.uniquindio.triage_academy.model.entity.Usuario;
import com.uniquindio.triage_academy.model.enums.CanalOrigen;
import com.uniquindio.triage_academy.model.enums.EstadoSolicitud;
import com.uniquindio.triage_academy.model.enums.Prioridad;
import com.uniquindio.triage_academy.model.enums.RolUsuario;
import com.uniquindio.triage_academy.model.enums.TipoSolicitud;
import com.uniquindio.triage_academy.repository.HistorialRepository;
import com.uniquindio.triage_academy.repository.SolicitudRepository;
import com.uniquindio.triage_academy.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitudService - Tests unitarios")
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private HistorialRepository historialRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SolicitudService solicitudService;

    private UUID solicitudId;
    private UUID usuarioId;
    private UUID responsableId;
    private Usuario usuario;
    private Usuario responsable;
    private Solicitud solicitud;
    private HistorialSolicitud historial;

    @BeforeEach
    void setUp() {
        solicitudId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        responsableId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(usuarioId)
                .identificacion("1090000001")
                .nombre("Ana Torres")
                .correo("ana@uniquindio.edu.co")
                .contrasena("hash")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .build();

        responsable = Usuario.builder()
                .id(responsableId)
                .identificacion("1090000002")
                .nombre("Luis Gomez")
                .correo("luis@uniquindio.edu.co")
                .contrasena("hash")
                .rol(RolUsuario.ADMINISTRATIVO)
                .activo(true)
                .build();

        solicitud = new Solicitud();
        solicitud.setId(solicitudId);
        solicitud.setUsuario(usuario);
        solicitud.setResponsable(responsable);
        solicitud.setTipo(TipoSolicitud.HOMOLOGACION);
        solicitud.setDescripcion("Homologacion de asignaturas");
        solicitud.setCanalOrigen(CanalOrigen.CORREO);
        solicitud.setFechaRegistro(LocalDateTime.of(2026, 4, 18, 10, 0));
        solicitud.setEstado(EstadoSolicitud.REGISTRADA);
        solicitud.setPrioridad(Prioridad.ALTA);
        solicitud.setJustificacionPrioridad("Asignada automáticamente por tipo: HOMOLOGACION");

        historial = new HistorialSolicitud();
        historial.setId(UUID.randomUUID());
        historial.setSolicitud(solicitud);
        historial.setFechaCreacion(LocalDateTime.of(2026, 4, 18, 11, 0));
        historial.setAccion("CREACION");
        historial.setObservaciones("Solicitud registrada en el sistema");
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Crea una solicitud y registra su historial")
        void crear_exitoso() {
            // Arrange
            CrearSolicitudRequest request = buildCrearRequest();

            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
            when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(historialRepository.save(any(HistorialSolicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SolicitudResponse response = solicitudService.crear(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getIdUsuario()).isEqualTo(usuarioId);
            assertThat(response.getTipo()).isEqualTo(TipoSolicitud.HOMOLOGACION);
            assertThat(response.getEstado()).isEqualTo(EstadoSolicitud.REGISTRADA);
            assertThat(response.getPrioridad()).isEqualTo(Prioridad.ALTA);

            verify(usuarioRepository, times(1)).findById(usuarioId);
            verify(solicitudRepository, times(1)).save(any(Solicitud.class));
            verify(historialRepository, times(1)).save(any(HistorialSolicitud.class));
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("Retorna la lista de solicitudes con filtros")
        void listar_exitoso() {
            // Arrange
            when(solicitudRepository.findAll(any(Specification.class))).thenReturn(List.of(solicitud));

            // Act
            List<SolicitudResponse> response = solicitudService.listar(
                    EstadoSolicitud.REGISTRADA,
                    TipoSolicitud.HOMOLOGACION,
                    Prioridad.ALTA,
                    responsableId
            );

            // Assert
            assertThat(response).hasSize(1);
            assertThat(response.getFirst().getId()).isEqualTo(solicitudId);
            assertThat(response.getFirst().getIdResponsable()).isEqualTo(responsableId);
            assertThat(response.getFirst().getPrioridad()).isEqualTo(Prioridad.ALTA);

            verify(solicitudRepository, times(1)).findAll(any(Specification.class));
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("Retorna una solicitud existente")
        void obtenerPorId_exitoso() {
            // Arrange
            when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));

            // Act
            SolicitudResponse response = solicitudService.obtenerPorId(solicitudId);

            // Assert
            assertThat(response.getId()).isEqualTo(solicitudId);
            assertThat(response.getDescripcion()).isEqualTo("Homologacion de asignaturas");
            assertThat(response.getCanalOrigen()).isEqualTo(CanalOrigen.CORREO);

            verify(solicitudRepository, times(1)).findById(solicitudId);
        }
    }

    @Nested
    @DisplayName("cambiarEstado()")
    class CambiarEstado {

        @Test
        @DisplayName("Actualiza el estado y registra historial")
        void cambiarEstado_exitoso() {
            // Arrange
            CambiarEstadoRequest request = buildCambiarEstadoRequest();

            when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(historialRepository.save(any(HistorialSolicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            solicitudService.cambiarEstado(solicitudId, request);

            // Assert
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.CLASIFICADA);

            verify(solicitudRepository, times(1)).findById(solicitudId);
            verify(solicitudRepository, times(1)).save(solicitud);
            verify(historialRepository, times(1)).save(any(HistorialSolicitud.class));
        }
    }

    @Nested
    @DisplayName("cerrar()")
    class Cerrar {

        @Test
        @DisplayName("Cierra una solicitud atendida y registra historial")
        void cerrar_exitoso() {
            // Arrange
            CerrarSolicitudRequest request = buildCerrarRequest();
            solicitud.setEstado(EstadoSolicitud.ATENDIDA);

            when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(historialRepository.save(any(HistorialSolicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            solicitudService.cerrar(solicitudId, request);

            // Assert
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.CERRADA);
            assertThat(solicitud.getObservacionCierre()).isEqualTo("Solicitud finalizada correctamente");

            verify(solicitudRepository, times(1)).findById(solicitudId);
            verify(solicitudRepository, times(1)).save(solicitud);
            verify(historialRepository, times(1)).save(any(HistorialSolicitud.class));
        }
    }

    @Nested
    @DisplayName("obtenerHistorial()")
    class ObtenerHistorial {

        @Test
        @DisplayName("Retorna el historial de una solicitud existente")
        void obtenerHistorial_exitoso() {
            // Arrange
            when(solicitudRepository.existsById(solicitudId)).thenReturn(true);
            when(historialRepository.findBySolicitudId(solicitudId)).thenReturn(List.of(historial));

            // Act
            List<HistorialSolicitudResponse> response = solicitudService.obtenerHistorial(solicitudId);

            // Assert
            assertThat(response).hasSize(1);
            assertThat(response.getFirst().getIdSolicitud()).isEqualTo(solicitudId);
            assertThat(response.getFirst().getAccion()).isEqualTo("CREACION");
            assertThat(response.getFirst().getObservaciones()).isEqualTo("Solicitud registrada en el sistema");

            verify(solicitudRepository, times(1)).existsById(solicitudId);
            verify(historialRepository, times(1)).findBySolicitudId(solicitudId);
        }
    }

    private CrearSolicitudRequest buildCrearRequest() {
        CrearSolicitudRequest request = new CrearSolicitudRequest();
        ReflectionTestUtils.setField(request, "idUsuario", usuarioId);
        ReflectionTestUtils.setField(request, "tipo", TipoSolicitud.HOMOLOGACION);
        ReflectionTestUtils.setField(request, "descripcion", "Homologacion de asignaturas");
        ReflectionTestUtils.setField(request, "canalOrigen", CanalOrigen.CORREO);
        return request;
    }

    private CambiarEstadoRequest buildCambiarEstadoRequest() {
        CambiarEstadoRequest request = new CambiarEstadoRequest();
        ReflectionTestUtils.setField(request, "nuevoEstado", EstadoSolicitud.CLASIFICADA);
        ReflectionTestUtils.setField(request, "observaciones", "Clasificacion inicial completada");
        return request;
    }

    private CerrarSolicitudRequest buildCerrarRequest() {
        CerrarSolicitudRequest request = new CerrarSolicitudRequest();
        ReflectionTestUtils.setField(request, "observacionCierre", "Solicitud finalizada correctamente");
        return request;
    }
}
