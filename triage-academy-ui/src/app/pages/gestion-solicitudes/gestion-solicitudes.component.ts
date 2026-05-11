import { Component, inject, OnInit, signal } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { SolicitudesTableComponent } from '../../components/molecules/solicitudes-table/solicitudes-table.component';
import { HistorialModalComponent } from '../../components/organisms/historial-modal/historial-modal.component';
import { SolicitudesService } from '../../core/services/solicitudes.service';
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
    HistorialModalComponent,
  ],
  templateUrl: './gestion-solicitudes.component.html',
})
export class GestionSolicitudesComponent implements OnInit {
  private solicitudesService = inject(SolicitudesService);

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-administrativo' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestionar solicitudes', route: '/gestion-solicitudes' },
  ];

  solicitudes = signal<Solicitud[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  selectedSolicitud = signal<Solicitud | null>(null);
  historial = signal<HistorialItem[]>([]);
  historialLoading = signal(false);
  historialError = signal<string | null>(null);

  ngOnInit(): void {
    this.solicitudesService.getSolicitudes().subscribe({
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
}
