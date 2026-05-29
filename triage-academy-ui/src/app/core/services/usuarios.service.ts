import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UsuarioResponse } from '../shared/models/auth.model';
import { RegistrarAdministrativoRequest } from '../shared/models/usuarios.model';

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private http = inject(HttpClient);
  private readonly BASE_URL = environment.apiUrl;

  getAdministrativos(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(`${this.BASE_URL}/usuarios/administrativos`);
  }

  buscarEstudiantes(cedula: string): Observable<UsuarioResponse[]> {
    const params = new HttpParams().set('cedula', cedula);
    return this.http.get<UsuarioResponse[]>(`${this.BASE_URL}/usuarios/estudiantes`, { params });
  }

  crearAdministrativo(data: RegistrarAdministrativoRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(`${this.BASE_URL}/usuarios/administrativos`, data).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    const errorMessages: Record<number, string> = {
      400: 'Datos inválidos o correo/identificación ya registrados',
      401: 'No autorizado',
      403: 'No tienes permisos para registrar administrativos',
      500: 'Error interno del servidor',
    };
    const message = errorMessages[error.status] ?? 'Error inesperado, intenta de nuevo';
    return throwError(() => new Error(message));
  }
}
