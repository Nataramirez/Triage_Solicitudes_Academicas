import { Routes } from '@angular/router';
import { noAuthGuard } from './core/guards/no-auth.guard';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.page').then(m => m.RegisterPage),
    canActivate: [noAuthGuard]
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.page').then(m => m.LoginPage),
    canActivate: [noAuthGuard]
  },
  {
    path: 'home',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent),
    canActivate: [authGuard, roleGuard('ESTUDIANTE')]
  },
  {
    path: 'home-administrativo',
    loadComponent: () => import('./pages/home-administrativo/home-administrativo.component').then(m => m.HomeAdministrativoComponent),
    canActivate: [authGuard, roleGuard('ADMINISTRATIVO')]
  },
  {
    path: 'home-director',
    loadComponent: () => import('./pages/home-director/home-director.component').then(m => m.HomeDirectorComponent),
    canActivate: [authGuard, roleGuard('DIRECTOR')]
  },
  {
    path: 'gestion-administrativos',
    loadComponent: () => import('./pages/gestion-administrativos/gestion-administrativos.component').then(m => m.GestionAdministrativosComponent),
    canActivate: [authGuard, roleGuard('DIRECTOR')]
  }
];
