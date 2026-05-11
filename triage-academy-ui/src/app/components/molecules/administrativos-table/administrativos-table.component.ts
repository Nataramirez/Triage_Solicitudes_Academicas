import { Component, input } from '@angular/core';
import { UsuarioResponse } from '../../../core/shared/models/auth.model';
import { BadgeComponent } from '../../atoms/badge/badge.component';

@Component({
  selector: 'app-administrativos-table',
  standalone: true,
  imports: [BadgeComponent],
  templateUrl: './administrativos-table.component.html',
})
export class AdministrativosTableComponent {
  administrativos = input.required<UsuarioResponse[]>();
}
