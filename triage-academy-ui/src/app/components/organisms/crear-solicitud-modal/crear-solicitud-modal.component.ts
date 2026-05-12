import { Component, inject, input, output, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonComponent } from '../../atoms/button/button.component';
import { SelectComponent } from '../../atoms/select/select.component';
import { CrearSolicitudRequest } from '../../../core/shared/models/solicitud.model';
import { UsuariosService } from '../../../core/services/usuarios.service';
import { UsuarioResponse } from '../../../core/shared/models/auth.model';

export type CrearSolicitudPayload = CrearSolicitudRequest;

@Component({
  selector: 'app-crear-solicitud-modal',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, ButtonComponent, SelectComponent],
  templateUrl: './crear-solicitud-modal.component.html',
})
export class CrearSolicitudModalComponent {
  private usuariosService = inject(UsuariosService);

  loading = input<boolean>(false);
  error = input<string | null>(null);

  closed = output<void>();
  submitted = output<CrearSolicitudPayload>();

  tipoOptions = ['REGISTRO_ASIGNATURAS', 'HOMOLOGACION', 'CANCELACION_ASIGNATURAS', 'SOLICITUD_CUPOS', 'CONSULTA_ACADEMICA'];
  canalOptions = ['CSU', 'CORREO', 'SAC', 'TELEFONICO'];

  cedulaInput = '';
  buscando = signal(false);
  buscarError = signal<string | null>(null);
  resultados = signal<UsuarioResponse[]>([]);
  estudianteSeleccionado = signal<UsuarioResponse | null>(null);
  sinResultados = signal(false);

  form = new FormGroup({
    idUsuario: new FormControl('', [Validators.required]),
    tipo: new FormControl('', [Validators.required]),
    descripcion: new FormControl('', [Validators.required, Validators.minLength(10)]),
    canalOrigen: new FormControl('', [Validators.required]),
  });

  buscarEstudiantes(): void {
    if (!this.cedulaInput.trim()) return;
    this.buscando.set(true);
    this.buscarError.set(null);
    this.sinResultados.set(false);
    this.resultados.set([]);
    this.estudianteSeleccionado.set(null);
    this.form.controls.idUsuario.setValue('');

    this.usuariosService.buscarEstudiantes(this.cedulaInput.trim()).subscribe({
      next: (data) => {
        this.resultados.set(data.slice(0, 4));
        this.sinResultados.set(data.length === 0);
        this.buscando.set(false);
      },
      error: () => {
        this.buscarError.set('No se pudieron cargar los estudiantes.');
        this.buscando.set(false);
      },
    });
  }

  seleccionarEstudiante(estudiante: UsuarioResponse): void {
    this.estudianteSeleccionado.set(estudiante);
    this.form.controls.idUsuario.setValue(estudiante.id);
    this.form.controls.idUsuario.markAsTouched();
    this.resultados.set([]);
  }

  limpiarSeleccion(): void {
    this.estudianteSeleccionado.set(null);
    this.form.controls.idUsuario.setValue('');
    this.cedulaInput = '';
    this.sinResultados.set(false);
  }

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
