import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-home-administrativo',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './home-administrativo.component.html',
})
export class HomeAdministrativoComponent {
  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-administrativo' },
    { label: 'Solicitudes', route: '/solicitudes' },
    { label: 'Usuarios', route: '/usuarios' },
  ];
}
