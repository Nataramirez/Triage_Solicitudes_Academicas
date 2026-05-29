import { Component, inject, OnInit, signal } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { AdministrativosTableComponent } from '../../components/molecules/administrativos-table/administrativos-table.component';
import { CrearAdministrativoModalComponent } from '../../components/organisms/crear-administrativo-modal/crear-administrativo-modal.component';
import { UsuariosService } from '../../core/services/usuarios.service';
import { NotificationService } from '../../core/services/notification.service';
import { UsuarioResponse } from '../../core/shared/models/auth.model';
import { RegistrarAdministrativoRequest } from '../../core/shared/models/usuarios.model';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-gestion-administrativos',
  standalone: true,
  imports: [
    NavbarComponent,
    SidebarComponent,
    AdministrativosTableComponent,
    CrearAdministrativoModalComponent,
  ],
  templateUrl: './gestion-administrativos.component.html',
})
export class GestionAdministrativosComponent implements OnInit {
  private usuariosService = inject(UsuariosService);
  private notification = inject(NotificationService);

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-director' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestión de administrativos', route: '/gestion-administrativos' },
  ];

  administrativos = signal<UsuarioResponse[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  modalOpen = signal(false);
  creating = signal(false);
  createError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadAdministrativos();
  }

  openCreateModal(): void {
    this.createError.set(null);
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    this.modalOpen.set(false);
    this.createError.set(null);
  }

  onCreate(data: RegistrarAdministrativoRequest): void {
    this.creating.set(true);
    this.createError.set(null);
    this.usuariosService.crearAdministrativo(data).subscribe({
      next: () => {
        this.creating.set(false);
        this.modalOpen.set(false);
        this.notification.show('Administrativo registrado exitosamente', 'success');
        this.loadAdministrativos();
      },
      error: (err: Error) => {
        this.creating.set(false);
        this.createError.set(err.message);
      },
    });
  }

  private loadAdministrativos(): void {
    this.loading.set(true);
    this.error.set(null);
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
