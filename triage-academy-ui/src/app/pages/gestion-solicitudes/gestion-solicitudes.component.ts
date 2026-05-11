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
import { SolicitudesService, SolicitudesQuery } from '../../core/services/solicitudes.service';
import { AuthService } from '../../core/services/auth-service.service';
import { NotificationService } from '../../core/services/notification.service';
import { Solicitud, CrearSolicitudRequest } from '../../core/shared/models/solicitud.model';
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
    const userId = this.auth.getUserId();
    if (!userId) {
      this.createError.set('No se pudo identificar el usuario.');
      return;
    }

    this.creating.set(true);
    this.createError.set(null);

    const payload: CrearSolicitudRequest = { ...data, idUsuario: userId };

    this.solicitudesService.crearSolicitud(payload).subscribe({
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
