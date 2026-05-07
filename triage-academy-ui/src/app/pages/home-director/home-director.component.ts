import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-home-director',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './home-director.component.html',
})
export class HomeDirectorComponent {
  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-director' },
    { label: 'Solicitudes', route: '/solicitudes' },
    { label: 'Usuarios', route: '/usuarios' },
  ];
}
