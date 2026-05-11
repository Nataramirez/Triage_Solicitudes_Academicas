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
