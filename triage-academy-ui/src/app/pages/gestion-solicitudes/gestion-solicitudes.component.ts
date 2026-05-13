import { Component, inject, OnInit, signal } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { SolicitudesTableComponent } from '../../components/molecules/solicitudes-table/solicitudes-table.component';
import {
  SolicitudesFiltersComponent,
  SolicitudesFilters,
} from '../../components/molecules/solicitudes-filters/solicitudes-filters.component';
import { HistorialModalComponent } from '../../components/organisms/historial-modal/historial-modal.component';
import {
  CrearSolicitudModalComponent,
  CrearSolicitudPayload,
} from '../../components/organisms/crear-solicitud-modal/crear-solicitud-modal.component';
import { CambiarEstadoModalComponent } from '../../components/organisms/cambiar-estado-modal/cambiar-estado-modal.component';
import { SolicitudesService, SolicitudesQuery } from '../../core/services/solicitudes.service';
import { AuthService } from '../../core/services/auth-service.service';
import { NotificationService } from '../../core/services/notification.service';
import { Solicitud } from '../../core/shared/models/solicitud.model';
import { HistorialItem } from '../../core/shared/models/historial.model';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-gestion-solicitudes',
  standalone: true,
  imports: [
    NavbarComponent,
    SidebarComponent,
    SolicitudesTableComponent,
    SolicitudesFiltersComponent,
    HistorialModalComponent,
    CrearSolicitudModalComponent,
    CambiarEstadoModalComponent,
  ],
  templateUrl: './gestion-solicitudes.component.html',
})
export class GestionSolicitudesComponent implements OnInit {
  private solicitudesService = inject(SolicitudesService);
  private auth = inject(AuthService);
  private notification = inject(NotificationService);

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-administrativo' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestionar solicitudes', route: '/gestion-solicitudes' },
  ];

  filters = signal<SolicitudesFilters>({
    estado: '',
    tipo: '',
    prioridad: '',
    misSolicitudes: false,
  });

  solicitudes = signal<Solicitud[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  selectedSolicitud = signal<Solicitud | null>(null);
  historial = signal<HistorialItem[]>([]);
  historialLoading = signal(false);
  historialError = signal<string | null>(null);

  modalOpen = signal(false);
  creating = signal(false);
  createError = signal<string | null>(null);

  solicitudParaEstado = signal<Solicitud | null>(null);
  cambiandoEstado = signal(false);
  cambioEstadoError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadSolicitudes();
  }

  onFiltersChange(filters: SolicitudesFilters): void {
    this.filters.set(filters);
    this.loadSolicitudes();
  }

  openCreateModal(): void {
    this.createError.set(null);
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    this.modalOpen.set(false);
    this.createError.set(null);
  }

  onCreate(data: CrearSolicitudPayload): void {
    this.creating.set(true);
    this.createError.set(null);

    this.solicitudesService.crearSolicitud(data).subscribe({
      next: () => {
        this.creating.set(false);
        this.modalOpen.set(false);
        this.notification.show('Solicitud creada exitosamente', 'success');
        this.loadSolicitudes();
      },
      error: (err: Error) => {
        this.creating.set(false);
        this.createError.set(err.message);
      },
    });
  }

  openHistorial(solicitud: Solicitud): void {
    this.selectedSolicitud.set(solicitud);
    this.historial.set([]);
    this.historialError.set(null);
    this.historialLoading.set(true);

    this.solicitudesService.getHistorial(solicitud.id).subscribe({
      next: (data) => {
        this.historial.set(data);
        this.historialLoading.set(false);
      },
      error: () => {
        this.historialError.set('No se pudo cargar el historial.');
        this.historialLoading.set(false);
      },
    });
  }

  closeHistorial(): void {
    this.selectedSolicitud.set(null);
  }

  openCambiarEstado(solicitud: Solicitud): void {
    this.cambioEstadoError.set(null);
    this.solicitudParaEstado.set(solicitud);
  }

  closeCambiarEstado(): void {
    this.solicitudParaEstado.set(null);
    this.cambioEstadoError.set(null);
  }

  onCambiarEstado(observaciones: string): void {
    const solicitud = this.solicitudParaEstado();
    if (!solicitud) return;

    this.cambiandoEstado.set(true);
    this.cambioEstadoError.set(null);

    const siguiente: Record<string, string> = {
      REGISTRADA: 'CLASIFICADA',
      CLASIFICADA: 'EN_ATENCION',
      EN_ATENCION: 'ATENDIDA',
    };

    const operacion$ = solicitud.estado === 'ATENDIDA'
      ? this.solicitudesService.cerrarSolicitud(solicitud.id, { observacionCierre: observaciones })
      : this.solicitudesService.cambiarEstado(solicitud.id, { nuevoEstado: siguiente[solicitud.estado], observaciones });

    operacion$.subscribe({
      next: () => {
        this.cambiandoEstado.set(false);
        this.solicitudParaEstado.set(null);
        this.notification.show('Estado actualizado correctamente', 'success');
        this.loadSolicitudes();
      },
      error: (err: Error) => {
        this.cambiandoEstado.set(false);
        this.cambioEstadoError.set(err.message);
      },
    });
  }

  private loadSolicitudes(): void {
    const f = this.filters();
    const userId = this.auth.getUserId();
    const query: SolicitudesQuery = {
      estado: f.estado || undefined,
      tipo: f.tipo || undefined,
      prioridad: f.prioridad || undefined,
      idResponsable: f.misSolicitudes && userId ? userId : undefined,
    };

    this.loading.set(true);
    this.error.set(null);
    this.solicitudesService.getSolicitudes(query).subscribe({
      next: (data) => {
        this.solicitudes.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las solicitudes.');
        this.loading.set(false);
      },
    });
  }
}
