import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';

export interface LoginCredentials {
  login: string;
  password: string;
}

export interface RegistrationDetails {
  username: string;
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private readonly http = inject(HttpClient);

  login(credentials: LoginCredentials): Observable<void> {
    return this.csrfToken().pipe(
      switchMap(() => this.http.post<void>('/api/auth/login', credentials)),
    );
  }

  register(details: RegistrationDetails): Observable<void> {
    return this.csrfToken().pipe(
      switchMap(() => this.http.post<void>('/api/auth/register', details)),
    );
  }

  private csrfToken(): Observable<void> {
    return this.http.get<void>('/api/auth/csrf');
  }
}
