import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthenticationService } from './authentication.service';

describe('AuthenticationService', () => {
  let service: AuthenticationService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthenticationService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('gets a CSRF token before posting login credentials', () => {
    service.login({ login: 'manu@example.com', password: 'Password1!' }).subscribe();

    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });
    const loginRequest = httpTesting.expectOne('/api/auth/login');

    expect(loginRequest.request.method).toBe('POST');
    expect(loginRequest.request.body).toEqual({
      login: 'manu@example.com',
      password: 'Password1!',
    });
    loginRequest.flush(null, { status: 204, statusText: 'No Content' });
  });

  it('gets a CSRF token before posting registration details', () => {
    service
      .register({ username: 'manu', email: 'manu@example.com', password: 'Password1!' })
      .subscribe();

    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });
    const registerRequest = httpTesting.expectOne('/api/auth/register');

    expect(registerRequest.request.method).toBe('POST');
    expect(registerRequest.request.body).toEqual({
      username: 'manu',
      email: 'manu@example.com',
      password: 'Password1!',
    });
    registerRequest.flush(null, { status: 201, statusText: 'Created' });
  });
});
