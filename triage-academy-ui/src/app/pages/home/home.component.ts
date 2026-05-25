import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SolicitudCardComponent } from '../../components/molecules/solicitud-card/solicitud-card.component';
import { HistorialModalComponent } from '../../components/organisms/historial-modal/historial-modal.component';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { AuthService } from '../../core/services/auth-service.service';
import { Solicitud } from '../../core/shared/models/solicitud.model';
import { HistorialItem } from '../../core/shared/models/historial.model';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NavbarComponent, SolicitudCardComponent, HistorialModalComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  private solicitudesService = inject(SolicitudesService);
  private authService = inject(AuthService);

  userName = this.authService.getUserName() ?? 'Estudiante';
  userRole = 'Estudiante';

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home' },
  ];

  solicitudes = signal<Solicitud[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  enProcesoCount = computed(() =>
    this.solicitudes().filter(s => s.estado === 'PENDIENTE' || s.estado === 'EN_REVISION').length
  );
  resueltasCount = computed(() =>
    this.solicitudes().filter(s => s.estado === 'RESUELTA').length
  );

  selectedSolicitud = signal<Solicitud | null>(null);
  historial = signal<HistorialItem[]>([]);
  historialLoading = signal(false);
  historialError = signal<string | null>(null);

  ngOnInit(): void {
    this.solicitudesService.getSolicitudesEstudiante().subscribe({
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

    this.solicitudesService.getHistorialEstudiante(solicitud.id).subscribe({
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
