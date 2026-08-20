import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SessionService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('stores the current user when the backend confirms the session', async () => {
    const restoredSession = firstValueFrom(service.restoreSession());

    const request = httpTesting.expectOne('/api/users/me');
    expect(request.request.method).toBe('GET');
    request.flush({ id: 1, username: 'manu', email: 'manu@example.com' });

    await expect(restoredSession).resolves.toBe(true);
    expect(service.currentUser()).toEqual({ id: 1, username: 'manu', email: 'manu@example.com' });
  });

  it('clears the current user when the backend rejects the session', async () => {
    const confirmedSession = firstValueFrom(service.restoreSession());
    httpTesting
      .expectOne('/api/users/me')
      .flush({ id: 1, username: 'manu', email: 'manu@example.com' });
    await confirmedSession;

    const rejectedSession = firstValueFrom(service.restoreSession());
    httpTesting.expectOne('/api/users/me').flush(null, { status: 401, statusText: 'Unauthorized' });

    await expect(rejectedSession).resolves.toBe(false);
    expect(service.currentUser()).toBeNull();
  });
});
