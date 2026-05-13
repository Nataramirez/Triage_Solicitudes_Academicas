export interface Solicitud {
  id: string;
  idUsuario: string;
  idResponsable: string | null;
  tipo: string;
  descripcion: string;
  estado: string;
  prioridad: string;
  canalOrigen: string;
  fechaRegistro: string;
  justificacionPrioridad: string;
  observacionCierre: string | null;
}

export interface CrearSolicitudRequest {
  idUsuario: string;
  tipo: string;
  descripcion: string;
  canalOrigen: string;
}

export interface CambiarEstadoRequest {
  nuevoEstado: string;
  observaciones?: string | null;
}

export interface CerrarSolicitudRequest {
  observacionCierre: string;
}
