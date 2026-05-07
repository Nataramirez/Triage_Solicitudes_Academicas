import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse, UserRegisterRequest, LoginRequest } from '../shared/models/auth.model';
import { catchError, Observable, throwError, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly TOKEN_KEY = 'auth_token';
  private readonly BASE_URL = `${environment.apiUrl}`;

  register(data: UserRegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.BASE_URL}/autenticacion/registro`, data).pipe(
      tap(response => this.saveSession(response)),
      catchError(this.handleError)
    );
  }

  login(body: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.BASE_URL}/autenticacion/iniciar-sesion`, body).pipe(
      tap(response => this.saveSession(response)),
      catchError(this.handleError)
    );
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.accessToken);
    localStorage.setItem('user_name', response.usuario.nombre);
    localStorage.setItem('user_id', response.usuario.id);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  private handleError(error: HttpErrorResponse): Observable<never> {

    const errorMessages: Record<number, string> = {
      400: 'Los datos enviados no son válidos',
      401: 'No autorizado',
      403: 'No tienes permisos para realizar esta acción',
      409: 'El correo ya está registrado',
      500: 'Error interno del servidor'
    };

    const message = errorMessages[error.status] ?? 'Error inesperado, intenta de nuevo';
    return throwError(() => new Error(message));
  }

}