import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { firstValueFrom, Observable, of } from 'rxjs';
import { SessionService } from './session.service';
import { unknownRouteGuard } from './unknown-route.guard';

describe('unknownRouteGuard', () => {
  const restoreSession = vi.fn<SessionService['restoreSession']>();

  beforeEach(() => {
    restoreSession.mockReset();
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionService, useValue: { restoreSession } }],
    });
  });

  it('allows an authenticated user to see the not found page', async () => {
    restoreSession.mockReturnValue(of(true));

    const result = await runGuard();

    expect(result).toBe(true);
  });

  it('redirects an unauthenticated user to the home page', async () => {
    restoreSession.mockReturnValue(of(false));

    const result = await runGuard();
    const router = TestBed.inject(Router);

    expect(router.serializeUrl(result as UrlTree)).toBe('/');
  });

  function runGuard(): Promise<boolean | UrlTree> {
    const guardResult = TestBed.runInInjectionContext(() =>
      unknownRouteGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    ) as Observable<boolean | UrlTree>;

    return firstValueFrom(guardResult);
  }
});
