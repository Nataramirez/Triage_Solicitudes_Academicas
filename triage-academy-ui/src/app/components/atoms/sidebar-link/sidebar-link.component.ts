import { Component, input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar-link',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar-link.component.html',
})
export class SidebarLinkComponent {
  label = input.required<string>();
  route = input.required<string>();
}
