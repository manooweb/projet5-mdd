import { provideLocationMocks } from '@angular/common/testing';
import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AuthenticationService } from '../auth/authentication.service';
import { CurrentUser } from '../auth/models/current-user';
import { SessionService } from '../auth/session.service';
import { NotFoundComponent } from './not-found.component';

describe('NotFoundComponent', () => {
  it('keeps the application navigation and offers a return to posts', async () => {
    const currentUser = signal<CurrentUser | null>({
      id: 1,
      username: 'demo',
      email: 'demo@mdd.net',
    });
    await TestBed.configureTestingModule({
      imports: [NotFoundComponent],
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        { provide: AuthenticationService, useValue: { logout: vi.fn(() => of(void 0)) } },
        { provide: SessionService, useValue: { currentUser, clearSession: vi.fn() } },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(NotFoundComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    expect(hostElement.querySelector('h1')?.textContent?.trim()).toBe('Page introuvable');
    expect(hostElement.querySelector('app-header')).not.toBeNull();
    expect(hostElement.querySelector('main a[href="/posts"]')?.textContent?.trim()).toBe(
      'Retour aux articles',
    );
  });
});
