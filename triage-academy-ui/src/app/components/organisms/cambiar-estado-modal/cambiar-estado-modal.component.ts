import { Component, computed, input, output } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Solicitud } from '../../../core/shared/models/solicitud.model';
import { BadgeComponent, BadgeVariant } from '../../atoms/badge/badge.component';

const SIGUIENTE_ESTADO: Record<string, string> = {
  REGISTRADA: 'CLASIFICADA',
  CLASIFICADA: 'EN_ATENCION',
  EN_ATENCION: 'ATENDIDA',
  ATENDIDA: 'CERRADA',
};

const SIGUIENTE_ESTADO_VARIANT: Record<string, BadgeVariant> = {
  CLASIFICADA: 'default',
  EN_ATENCION: 'warning',
  ATENDIDA: 'success',
  CERRADA: 'danger',
};

@Component({
  selector: 'app-cambiar-estado-modal',
  standalone: true,
  imports: [ReactiveFormsModule, BadgeComponent],
  templateUrl: './cambiar-estado-modal.component.html',
})
export class CambiarEstadoModalComponent {
  solicitud = input.required<Solicitud>();
  loading = input<boolean>(false);
  error = input<string | null>(null);

  closed = output<void>();
  submitted = output<string>();

  observaciones = new FormControl('', [Validators.required, Validators.minLength(5)]);

  siguienteEstado = computed(() => SIGUIENTE_ESTADO[this.solicitud().estado] ?? '');
  esCierre = computed(() => this.solicitud().estado === 'ATENDIDA');
  siguienteEstadoVariant = computed((): BadgeVariant => SIGUIENTE_ESTADO_VARIANT[this.siguienteEstado()] ?? 'default');

  onClose(): void {
    this.closed.emit();
  }

  submit(): void {
    this.observaciones.markAsTouched();
    if (this.observaciones.invalid) return;
    this.submitted.emit(this.observaciones.value!);
  }
}
