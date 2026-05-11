import { Component, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonComponent } from '../../atoms/button/button.component';
import { SelectComponent } from '../../atoms/select/select.component';
import { CrearSolicitudRequest } from '../../../core/shared/models/solicitud.model';

export type CrearSolicitudPayload = Omit<CrearSolicitudRequest, 'idUsuario'>;

@Component({
  selector: 'app-crear-solicitud-modal',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent, SelectComponent],
  templateUrl: './crear-solicitud-modal.component.html',
})
export class CrearSolicitudModalComponent {
  loading = input<boolean>(false);
  error = input<string | null>(null);

  closed = output<void>();
  submitted = output<CrearSolicitudPayload>();

  tipoOptions = ['REGISTRO_ASIGNATURAS', 'HOMOLOGACION', 'CANCELACION_ASIGNATURAS', 'SOLICITUD_CUPOS', 'CONSULTA_ACADEMICA'];
  canalOptions = ['CSU', 'CORREO', 'SAC', 'TELEFONICO'];

  form = new FormGroup({
    tipo: new FormControl('', [Validators.required]),
    descripcion: new FormControl('', [Validators.required, Validators.minLength(10)]),
    canalOrigen: new FormControl('', [Validators.required]),
  });

  setTipo(value: string): void {
    this.form.controls.tipo.setValue(value);
    this.form.controls.tipo.markAsTouched();
  }

  setCanalOrigen(value: string): void {
    this.form.controls.canalOrigen.setValue(value);
    this.form.controls.canalOrigen.markAsTouched();
  }

  onClose(): void {
    this.closed.emit();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitted.emit(this.form.value as CrearSolicitudPayload);
  }
}
