import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './navbar.component.html'
})
/**
 * @description Navbar molecule that displays the main navigation with a home link and a logout button
 * @export
 * @class NavbarComponent
 */
export class NavbarComponent {
  auth = inject(AuthService);
}
