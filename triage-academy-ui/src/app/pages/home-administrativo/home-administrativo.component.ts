import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { AuthService } from '../../core/services/auth-service.service';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { Solicitud } from '../../core/shared/models/solicitud.model';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-home-administrativo',
  standalone: true,
  imports: [NavbarComponent, SidebarComponent, RouterLink],
  templateUrl: './home-administrativo.component.html',
})
export class HomeAdministrativoComponent implements OnInit {
  private authService = inject(AuthService);
  private solicitudesService = inject(SolicitudesService);

  userName = this.authService.getUserName() ?? 'Administrativo';
  userRole = 'Administrativo';

  solicitudes = signal<Solicitud[]>([]);
  loadingStats = signal(true);

  asignadasCount = computed(() =>
    this.solicitudes().filter(s => s.estado !== 'RESUELTA').length
  );
  resueltasCount = computed(() =>
    this.solicitudes().filter(s => s.estado === 'RESUELTA').length
  );

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-administrativo' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestionar solicitudes', route: '/gestion-solicitudes' },
  ];

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.solicitudesService.getSolicitudes({ idResponsable: userId }).subscribe({
        next: (data) => {
          this.solicitudes.set(data);
          this.loadingStats.set(false);
        },
        error: () => this.loadingStats.set(false),
      });
    } else {
      this.loadingStats.set(false);
    }
  }
}
