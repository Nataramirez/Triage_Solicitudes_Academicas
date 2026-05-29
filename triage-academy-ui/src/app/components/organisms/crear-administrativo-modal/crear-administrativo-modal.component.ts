import { Component, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonComponent } from '../../atoms/button/button.component';
import { FormFieldComponent } from '../../molecules/form-field/form-field.component';
import { RegistrarAdministrativoRequest } from '../../../core/shared/models/usuarios.model';

@Component({
  selector: 'app-crear-administrativo-modal',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent, FormFieldComponent],
  templateUrl: './crear-administrativo-modal.component.html',
})
export class CrearAdministrativoModalComponent {
  loading = input<boolean>(false);
  error = input<string | null>(null);

  closed = output<void>();
  submitted = output<RegistrarAdministrativoRequest>();

  form = new FormGroup({
    identificacion: new FormControl('', [Validators.required, Validators.minLength(3)]),
    nombre: new FormControl('', [Validators.required, Validators.minLength(3), Validators.pattern('[A-Za-zÁÉÍÓÚáéíóúÜüÑñ ]+')]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    contrasena: new FormControl('', [Validators.required, Validators.minLength(8)]),
  });

  onClose(): void {
    this.closed.emit();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitted.emit(this.form.value as RegistrarAdministrativoRequest);
  }
}
