import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Service, signal } from '@angular/core';
import { catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { CurrentUser } from './models/current-user';

export interface UpdateCurrentUserDetails {
  username: string;
  email: string;
  password: string;
}

@Service()
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

  updateCurrentUser(details: UpdateCurrentUserDetails): Observable<void> {
    return this.http.patch<void>('/api/users/me', details).pipe(
      tap(() => {
        const currentUser = this.currentUser();

        if (currentUser) {
          this.currentUser.set({
            ...currentUser,
            username: details.username,
            email: details.email,
          });
        }
      }),
    );
  }

  clearSession(): void {
    this.currentUser.set(null);
  }
}
