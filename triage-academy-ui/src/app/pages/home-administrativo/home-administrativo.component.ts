import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-home-administrativo',
  standalone: true,
  imports: [NavbarComponent, SidebarComponent],
  templateUrl: './home-administrativo.component.html',
})
export class HomeAdministrativoComponent {
  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-administrativo' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestionar solicitudes', route: '/gestion-solicitudes' },
  ];
}
