import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthenticationService } from '../../auth/authentication.service';
import { CurrentUser } from '../../auth/models/current-user';
import { SessionService } from '../../auth/session.service';
import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  const currentUser = signal<CurrentUser | null>(null);
  const logout = vi.fn<AuthenticationService['logout']>();
  const clearSession = vi.fn<SessionService['clearSession']>();

  beforeEach(async () => {
    currentUser.set(null);
    logout.mockReturnValue(of(void 0));
    clearSession.mockClear();

    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [
        provideRouter([]),
        { provide: AuthenticationService, useValue: { logout } },
        { provide: SessionService, useValue: { currentUser, clearSession } },
      ],
    }).compileComponents();
  });

  it('shows the navigation menu trigger for interior pages', () => {
    const fixture = TestBed.createComponent(HeaderComponent);
    fixture.detectChanges();

    const button = (fixture.nativeElement as HTMLElement).querySelector('button');

    expect(button?.getAttribute('aria-label')).toBe('Ouvrir le menu de navigation');
  });

  it('hides the navigation menu trigger for authentication pages', () => {
    const fixture = TestBed.createComponent(HeaderComponent);
    fixture.componentRef.setInput('authentication', true);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('button')).toBeNull();
  });

  it('links the logo to posts and offers logout when a user is logged in', () => {
    currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
    const fixture = TestBed.createComponent(HeaderComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const logoLink = hostElement.querySelector<HTMLAnchorElement>('a[aria-label="Accueil MDD"]');
    const logoutButton = hostElement.querySelector<HTMLButtonElement>(
      'button[aria-label="Se déconnecter"]',
    );

    expect(logoLink).not.toBeNull();
    expect(logoLink!.getAttribute('href')).toBe('/posts');
    expect(logoutButton?.textContent?.trim()).toBe('Se déconnecter');
  });

  it('clears the session and redirects to login after logout succeeds', () => {
    currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
    const fixture = TestBed.createComponent(HeaderComponent);
    const router = TestBed.inject(Router);
    const navigateByUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    fixture.detectChanges();

    const logoutButton = (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>(
      'button[aria-label="Se déconnecter"]',
    );
    logoutButton!.click();

    expect(logout).toHaveBeenCalledOnce();
    expect(clearSession).toHaveBeenCalledOnce();
    expect(navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
