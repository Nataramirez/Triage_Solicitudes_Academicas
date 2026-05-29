import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './input.component.html'
})
/**
 * @description Reusable text input atom integrated with Angular Reactive Forms
 * @export
 * @class InputComponent
 */
export class InputComponent {
  id = input.required<string>();
  type = input<string>('text');
  placeholder = input<string>('');
  control = input<FormControl>(new FormControl(''));
}
