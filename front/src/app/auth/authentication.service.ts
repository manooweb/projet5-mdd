import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
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

@Service()
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

  logout(): Observable<void> {
    return this.csrfToken().pipe(switchMap(() => this.http.post<void>('/api/auth/logout', null)));
  }

  private csrfToken(): Observable<void> {
    return this.http.get<void>('/api/auth/csrf');
  }
}
