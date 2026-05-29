
export interface UserRegisterRequest {
  identificacion: string;
  nombre: string;
  correo: string;
  contrasena: string;
}

export interface LoginRequest {
  correo: string;
  contrasena: string;
}

export interface UsuarioResponse {
  id: string;
  identificacion: string;
  nombre: string;
  correo: string;
  rol: string;
  activo: boolean;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  usuario: UsuarioResponse;
}
