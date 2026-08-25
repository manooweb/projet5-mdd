import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { SessionService } from './session.service';

describe('SessionService integration', () => {
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

  it('stores the user returned by the session endpoint', async () => {
    const restoration = firstValueFrom(service.restoreSession());

    httpTesting
      .expectOne('/api/users/me')
      .flush({ id: 1, username: 'manu', email: 'manu@example.com' });

    await expect(restoration).resolves.toBe(true);
    expect(service.currentUser()).toEqual({ id: 1, username: 'manu', email: 'manu@example.com' });
  });

  it('clears a previously restored user when the session endpoint returns 401', async () => {
    service.currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
    const restoration = firstValueFrom(service.restoreSession());

    httpTesting.expectOne('/api/users/me').flush(null, { status: 401, statusText: 'Unauthorized' });

    await expect(restoration).resolves.toBe(false);
    expect(service.currentUser()).toBeNull();
  });

  it('keeps unexpected restoration failures available to the caller', async () => {
    const restoration = firstValueFrom(service.restoreSession());

    httpTesting
      .expectOne('/api/users/me')
      .flush(null, { status: 503, statusText: 'Service Unavailable' });

    await expect(restoration).rejects.toMatchObject({ status: 503 });
  });

  it('updates the in-memory user after the profile endpoint accepts the change', async () => {
    service.currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
    const update = firstValueFrom(
      service.updateCurrentUser({
        username: 'manu-updated',
        email: 'updated@example.com',
        password: '',
      }),
    );

    const request = httpTesting.expectOne('/api/users/me');
    expect(request.request.method).toBe('PATCH');
    request.flush(null, { status: 204, statusText: 'No Content' });

    await expect(update).resolves.toBeNull();
    expect(service.currentUser()).toEqual({
      id: 1,
      username: 'manu-updated',
      email: 'updated@example.com',
    });
  });
});
