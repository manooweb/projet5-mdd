import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthenticationService } from '../../auth/authentication.service';
import { CurrentUser } from '../../auth/models/current-user';
import { SessionService } from '../../auth/session.service';
import { LogoutButtonComponent } from './logout-button.component';

describe('LogoutButtonComponent', () => {
  const currentUser = signal<CurrentUser | null>(null);
  const logout = vi.fn<AuthenticationService['logout']>(() => of(void 0));
  const clearSession = vi.fn<SessionService['clearSession']>();

  beforeEach(async () => {
    currentUser.set(null);
    logout.mockClear();
    clearSession.mockClear();

    await TestBed.configureTestingModule({
      imports: [LogoutButtonComponent],
      providers: [
        provideRouter([]),
        { provide: AuthenticationService, useValue: { logout } },
        { provide: SessionService, useValue: { currentUser, clearSession } },
      ],
    }).compileComponents();
  });

  it('only displays the action for an authenticated user', () => {
    const fixture = TestBed.createComponent(LogoutButtonComponent);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('button')).toBeNull();

    currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
    fixture.detectChanges();

    expect(
      (fixture.nativeElement as HTMLElement).querySelector('button')?.textContent?.trim(),
    ).toBe('Se déconnecter');
  });

  it('clears the session and redirects to login after logout succeeds', () => {
    currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
    const fixture = TestBed.createComponent(LogoutButtonComponent);
    const router = TestBed.inject(Router);
    const navigateByUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    fixture.detectChanges();

    (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>('button')!.click();

    expect(logout).toHaveBeenCalledOnce();
    expect(clearSession).toHaveBeenCalledOnce();
    expect(navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
