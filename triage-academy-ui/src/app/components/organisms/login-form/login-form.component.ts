import { Component, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginRequest } from '../../../core/shared/models/auth.model';
import { ButtonComponent } from '../../atoms/button/button.component'; 
import { FormFieldComponent } from '../../molecules/form-field/form-field.component';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent, FormFieldComponent],
  templateUrl: './login-form.component.html'
})
/**
 * @description Login form organism with email and password fields and validation
 * @export
 * @class LoginFormComponent
 */
export class LoginFormComponent {
  
  loading = input<boolean>(false);
  formSubmit = output<LoginRequest>();

  form = new FormGroup({
    correo: new FormControl('', [Validators.required, Validators.email]),
    contrasena: new FormControl('', [Validators.required])
  });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.formSubmit.emit(this.form.value as LoginRequest);
  }
}
