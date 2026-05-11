import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Solicitud } from '../shared/models/solicitud.model';
import { HistorialItem } from '../shared/models/historial.model';

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
}
