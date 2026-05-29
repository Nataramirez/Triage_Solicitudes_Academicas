import { Component, input } from '@angular/core';

@Component({
  selector: 'app-label',
  imports: [],
  templateUrl: './label.component.html',
})
/**
 * @description Reusable label atom that associates a text label with a form input
 * @export
 * @class LabelComponent
 */
export class LabelComponent {
  inputId = input.required<string>();
  label = input.required<string>();
}