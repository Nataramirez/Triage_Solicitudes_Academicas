import { Component, inject, OnInit, signal } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { AdministrativosTableComponent } from '../../components/molecules/administrativos-table/administrativos-table.component';
import { UsuariosService } from '../../core/services/usuarios.service';
import { UsuarioResponse } from '../../core/shared/models/auth.model';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-gestion-administrativos',
  standalone: true,
  imports: [NavbarComponent, SidebarComponent, AdministrativosTableComponent],
  templateUrl: './gestion-administrativos.component.html',
})
export class GestionAdministrativosComponent implements OnInit {
  private usuariosService = inject(UsuariosService);

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-director' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestión de administrativos', route: '/gestion-administrativos' },
  ];

  administrativos = signal<UsuarioResponse[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.usuariosService.getAdministrativos().subscribe({
      next: (data) => {
        this.administrativos.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los administrativos.');
        this.loading.set(false);
      },
    });
  }
}
