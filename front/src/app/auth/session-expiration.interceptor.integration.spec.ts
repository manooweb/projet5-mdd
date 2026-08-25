import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SessionService } from './session.service';
import { sessionExpirationInterceptor } from './session-expiration.interceptor';

describe('session expiration integration', () => {
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

  it('clears an active session and returns the user to login after a protected request expires', () => {
    http.get('/api/posts').subscribe({ error: () => undefined });

    httpTesting.expectOne('/api/posts').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(sessionService.currentUser()).toBeNull();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });

  it('leaves a failed session restoration request to the route guard', () => {
    http.get('/api/users/me').subscribe({ error: () => undefined });

    httpTesting.expectOne('/api/users/me').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(sessionService.currentUser()).not.toBeNull();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });
});
