import { Component, inject, input } from '@angular/core';
import { NavItem } from '../../../core/shared/models/nav.model';
import { NavMenuComponent } from '../../molecules/nav-menu/nav-menu.component';
import { AuthService } from '../../../core/services/auth-service.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NavMenuComponent],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  title = input.required<string>();
  navItems = input.required<NavItem[]>();
  private auth = inject(AuthService);

  logout(): void {
    this.auth.removeToken();
  }
}
