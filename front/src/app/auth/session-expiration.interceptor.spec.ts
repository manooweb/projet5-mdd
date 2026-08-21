import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SessionService } from './session.service';
import { sessionExpirationInterceptor } from './session-expiration.interceptor';

describe('sessionExpirationInterceptor', () => {
  const router = { navigateByUrl: vi.fn().mockResolvedValue(true) };
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let sessionService: SessionService;

  beforeEach(() => {
    router.navigateByUrl.mockClear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([sessionExpirationInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);
    sessionService.currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
  });

  afterEach(() => httpTesting.verify());

  it('clears the session and redirects to login when a protected API returns 401', () => {
    http.get('/api/posts').subscribe({ error: () => undefined });

    httpTesting.expectOne('/api/posts').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(sessionService.currentUser()).toBeNull();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });

  it('leaves authentication endpoint errors to their caller', () => {
    http.post('/api/auth/login', {}).subscribe({ error: () => undefined });

    httpTesting
      .expectOne('/api/auth/login')
      .flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(sessionService.currentUser()).toEqual({
      id: 1,
      username: 'manu',
      email: 'manu@example.com',
    });
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });
});
