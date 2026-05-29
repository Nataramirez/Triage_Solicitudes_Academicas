import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth-service.service';

export const roleGuard = (requiredRole: string): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.getUserRole() === requiredRole) return true;
  return router.createUrlTree([auth.getHomeRoute()]);
};
