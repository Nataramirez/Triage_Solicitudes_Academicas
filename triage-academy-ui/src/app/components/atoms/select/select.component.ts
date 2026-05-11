import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-select',
  standalone: true,
  templateUrl: './select.component.html',
  styles: [`
    select {
      background-position: right 0.625rem center;
      background-repeat: no-repeat;
      background-size: 0.875rem;
    }
  `],
})
export class SelectComponent {
  value = input<string>('');
  placeholder = input.required<string>();
  options = input.required<string[]>();
  valueChange = output<string>();

  onChange(event: Event): void {
    this.valueChange.emit((event.target as HTMLSelectElement).value);
  }
}
