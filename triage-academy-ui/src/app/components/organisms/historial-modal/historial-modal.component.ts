import { Component, input, output } from '@angular/core';
import { HistorialItem } from '../../../core/shared/models/historial.model';
import { Solicitud } from '../../../core/shared/models/solicitud.model';
import { HistorialItemComponent } from '../../molecules/historial-item/historial-item.component';

@Component({
  selector: 'app-historial-modal',
  standalone: true,
  imports: [HistorialItemComponent],
  templateUrl: './historial-modal.component.html',
})
export class HistorialModalComponent {
  solicitud = input.required<Solicitud>();
  items = input.required<HistorialItem[]>();
  loading = input<boolean>(false);
  error = input<string | null>(null);

  closed = output<void>();

  onClose(): void {
    this.closed.emit();
  }
}
