import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { SessionService } from './session.service';

export const unknownRouteGuard: CanActivateFn = () => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  return sessionService
    .restoreSession()
    .pipe(map((isAuthenticated) => isAuthenticated || router.createUrlTree(['/'])));
};
