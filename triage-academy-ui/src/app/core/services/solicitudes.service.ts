import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Solicitud, CrearSolicitudRequest } from '../shared/models/solicitud.model';
import { HistorialItem } from '../shared/models/historial.model';

export interface SolicitudesQuery {
  estado?: string;
  tipo?: string;
  prioridad?: string;
  idResponsable?: string;
}

@Injectable({ providedIn: 'root' })
export class SolicitudesService {
  private http = inject(HttpClient);
  private readonly BASE_URL = environment.apiUrl;

  getSolicitudesEstudiante(): Observable<Solicitud[]> {
    return this.http.get<Solicitud[]>(`${this.BASE_URL}/solicitudes/estudiante`);
  }

  getHistorialEstudiante(idSolicitud: string): Observable<HistorialItem[]> {
    return this.http.get<HistorialItem[]>(`${this.BASE_URL}/solicitudes/estudiante/${idSolicitud}/historial`);
  }

  getSolicitudes(query: SolicitudesQuery = {}): Observable<Solicitud[]> {
    let params = new HttpParams();
    if (query.estado) params = params.set('estado', query.estado);
    if (query.tipo) params = params.set('tipo', query.tipo);
    if (query.prioridad) params = params.set('prioridad', query.prioridad);
    if (query.idResponsable) params = params.set('idResponsable', query.idResponsable);
    return this.http.get<Solicitud[]>(`${this.BASE_URL}/solicitudes`, { params });
  }

  getHistorial(idSolicitud: string): Observable<HistorialItem[]> {
    return this.http.get<HistorialItem[]>(`${this.BASE_URL}/solicitudes/${idSolicitud}/historial`);
  }

  crearSolicitud(data: CrearSolicitudRequest): Observable<Solicitud> {
    return this.http.post<Solicitud>(`${this.BASE_URL}/solicitudes`, data).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    const errorMessages: Record<number, string> = {
      400: 'Datos inválidos',
      401: 'No autorizado',
      404: 'Usuario no encontrado',
      500: 'Error interno del servidor',
    };
    const message = errorMessages[error.status] ?? 'Error inesperado, intenta de nuevo';
    return throwError(() => new Error(message));
  }
}
