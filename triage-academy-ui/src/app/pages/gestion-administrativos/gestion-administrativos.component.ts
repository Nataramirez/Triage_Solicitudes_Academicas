import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-gestion-administrativos',
  standalone: true,
  imports: [NavbarComponent, SidebarComponent],
  templateUrl: './gestion-administrativos.component.html',
})
export class GestionAdministrativosComponent {
  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-director' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestión de administrativos', route: '/gestion-administrativos' },
  ];
}
