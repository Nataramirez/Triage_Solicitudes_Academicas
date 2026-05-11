import { Component, inject, OnInit, signal } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SolicitudCardComponent } from '../../components/molecules/solicitud-card/solicitud-card.component';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { Solicitud } from '../../core/shared/models/solicitud.model';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NavbarComponent, SolicitudCardComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  private solicitudesService = inject(SolicitudesService);

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home' },
  ];

  solicitudes = signal<Solicitud[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

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
}
