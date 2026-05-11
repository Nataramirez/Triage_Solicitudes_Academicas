import { Component, input } from '@angular/core';
import { NavItem } from '../../../core/shared/models/nav.model';
import { SidebarLinkComponent } from '../../atoms/sidebar-link/sidebar-link.component';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [SidebarLinkComponent],
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  items = input.required<NavItem[]>();
}
