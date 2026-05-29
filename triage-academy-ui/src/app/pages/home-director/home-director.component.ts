import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../components/organisms/navbar/navbar.component';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar.component';
import { AuthService } from '../../core/services/auth-service.service';
import { NavItem } from '../../core/shared/models/nav.model';

@Component({
  selector: 'app-home-director',
  standalone: true,
  imports: [NavbarComponent, SidebarComponent, RouterLink],
  templateUrl: './home-director.component.html',
})
export class HomeDirectorComponent {
  private authService = inject(AuthService);

  userName = this.authService.getUserName() ?? 'Director';
  userRole = 'Director';

  navItems: NavItem[] = [
    { label: 'Inicio', route: '/home-director' },
  ];

  sidebarItems: NavItem[] = [
    { label: 'Gestión de administrativos', route: '/gestion-administrativos' },
  ];
}
