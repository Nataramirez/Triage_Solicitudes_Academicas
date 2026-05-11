import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UsuarioResponse } from '../shared/models/auth.model';

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private http = inject(HttpClient);
  private readonly BASE_URL = environment.apiUrl;

  getAdministrativos(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(`${this.BASE_URL}/usuarios/administrativos`);
  }
}
