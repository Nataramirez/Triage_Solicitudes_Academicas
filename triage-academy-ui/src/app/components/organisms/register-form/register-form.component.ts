import { Component, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonComponent } from '../../atoms/button/button.component';
import { FormFieldComponent } from '../../molecules/form-field/form-field.component';
import { UserRegisterRequest } from '../../../core/shared/models/auth.model';

@Component({
  selector: 'app-register-form',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent, FormFieldComponent],
  templateUrl: './register-form.component.html'
})
/**
 * @description Registration form organism with name, email, and password fields and validation
 * @export
 * @class RegisterFormComponent
 */
export class RegisterFormComponent {

  loading = input<boolean>(false);
  formSubmit = output<UserRegisterRequest>();

  form = new FormGroup({
    identificacion: new FormControl('', [Validators.required, Validators.minLength(3)]),
    nombre: new FormControl('', [Validators.required, Validators.minLength(3), Validators.pattern('[A-Za-zÁÉÍÓÚáéíóúÜüÑñ ]+')]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    contrasena: new FormControl('', [Validators.required, Validators.minLength(8)])
  });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.formSubmit.emit(this.form.value as UserRegisterRequest);
  }

}