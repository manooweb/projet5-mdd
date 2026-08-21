import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { SessionService } from './session.service';

export const sessionExpirationInterceptor: HttpInterceptorFn = (request, next) => {
  const router = inject(Router);
  const sessionService = inject(SessionService);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && isProtectedApiRequest(request.url)) {
        sessionService.clearSession();
        void router.navigateByUrl('/login');
      }

      return throwError(() => error);
    }),
  );
};

function isProtectedApiRequest(url: string): boolean {
  return !url.startsWith('/api/auth/') && url !== '/api/users/me';
}
