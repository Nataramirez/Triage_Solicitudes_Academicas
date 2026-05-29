import { Component, input, output } from '@angular/core';
import { SelectComponent } from '../../atoms/select/select.component';

export interface SolicitudesFilters {
  estado: string;
  tipo: string;
  prioridad: string;
  misSolicitudes: boolean;
}

@Component({
  selector: 'app-solicitudes-filters',
  standalone: true,
  imports: [SelectComponent],
  templateUrl: './solicitudes-filters.component.html',
})
export class SolicitudesFiltersComponent {
  filters = input.required<SolicitudesFilters>();
  filtersChange = output<SolicitudesFilters>();

  estadoOptions = ['REGISTRADA', 'CLASIFICADA', 'EN_ATENCION', 'ATENDIDA', 'CERRADA'];
  tipoOptions = ['REGISTRO_ASIGNATURAS', 'HOMOLOGACION', 'CANCELACION_ASIGNATURAS', 'SOLICITUD_CUPOS', 'CONSULTA_ACADEMICA'];
  prioridadOptions = ['CRITICA', 'ALTA', 'MEDIA', 'BAJA'];

  update(patch: Partial<SolicitudesFilters>): void {
    this.filtersChange.emit({ ...this.filters(), ...patch });
  }
}
