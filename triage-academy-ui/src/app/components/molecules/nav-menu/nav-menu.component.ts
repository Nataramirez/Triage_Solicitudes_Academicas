import { Component, input } from '@angular/core';
import { NavItem } from '../../../core/shared/models/nav.model';
import { NavLinkComponent } from '../../atoms/nav-link/nav-link.component';

@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [NavLinkComponent],
  templateUrl: './nav-menu.component.html',
})
export class NavMenuComponent {
  items = input.required<NavItem[]>();
}
