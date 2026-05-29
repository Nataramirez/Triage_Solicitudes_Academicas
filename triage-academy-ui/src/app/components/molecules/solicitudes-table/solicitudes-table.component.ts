import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Solicitud } from '../../../core/shared/models/solicitud.model';
import { BadgeComponent, BadgeVariant } from '../../atoms/badge/badge.component';

@Component({
  selector: 'app-solicitudes-table',
  standalone: true,
  imports: [DatePipe, BadgeComponent],
  templateUrl: './solicitudes-table.component.html',
})
export class SolicitudesTableComponent {
  solicitudes = input.required<Solicitud[]>();
  viewHistorial = output<Solicitud>();
  avanzarEstado = output<Solicitud>();

  estadoVariant(estado: string): BadgeVariant {
    const map: Record<string, BadgeVariant> = {
      REGISTRADA:  'info',
      CLASIFICADA: 'default',
      EN_ATENCION: 'warning',
      ATENDIDA:    'success',
      CERRADA:     'default',
    };
    return map[estado] ?? 'default';
  }

  prioridadVariant(prioridad: string): BadgeVariant {
    const map: Record<string, BadgeVariant> = {
      CRITICA: 'danger',
      ALTA:    'danger',
      MEDIA:   'warning',
      BAJA:    'success',
    };
    return map[prioridad] ?? 'default';
  }
}
