import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HistorialItem } from '../../../core/shared/models/historial.model';

@Component({
  selector: 'app-historial-item',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './historial-item.component.html',
})
export class HistorialItemComponent {
  item = input.required<HistorialItem>();
  isLast = input<boolean>(false);
}
