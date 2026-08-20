import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { CurrentUser } from './models/current-user';

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly http = inject(HttpClient);

  readonly currentUser = signal<CurrentUser | null>(null);

  restoreSession(): Observable<boolean> {
    return this.http.get<CurrentUser>('/api/users/me').pipe(
      tap((user) => this.currentUser.set(user)),
      map(() => true),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.currentUser.set(null);
          return of(false);
        }

        return throwError(() => error);
      }),
    );
  }
}
