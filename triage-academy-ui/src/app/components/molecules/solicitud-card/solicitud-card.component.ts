import { Component, computed, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Solicitud } from '../../../core/shared/models/solicitud.model';
import { BadgeComponent, BadgeVariant } from '../../atoms/badge/badge.component';

@Component({
  selector: 'app-solicitud-card',
  standalone: true,
  imports: [BadgeComponent, DatePipe],
  templateUrl: './solicitud-card.component.html',
})
export class SolicitudCardComponent {
  solicitud = input.required<Solicitud>();
  cardClick = output<Solicitud>();

  onClick(): void {
    this.cardClick.emit(this.solicitud());
  }

  estadoVariant = computed((): BadgeVariant => {
    const map: Record<string, BadgeVariant> = {
      REGISTRADA:  'info',
      CLASIFICADA: 'default',
      EN_ATENCION: 'warning',
      ATENDIDA:    'success',
      CERRADA:     'default',
    };
    return map[this.solicitud().estado] ?? 'default';
  });

  prioridadVariant = computed((): BadgeVariant => {
    const map: Record<string, BadgeVariant> = {
      ALTA:  'danger',
      MEDIA: 'warning',
      BAJA:  'success',
    };
    return map[this.solicitud().prioridad] ?? 'default';
  });
}
