import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AuthenticationService } from '../../auth/authentication.service';
import { CurrentUser } from '../../auth/models/current-user';
import { SessionService } from '../../auth/session.service';
import { MobileNavigationComponent } from './mobile-navigation.component';

describe('MobileNavigationComponent', () => {
  const currentUser = signal<CurrentUser | null>(null);

  beforeEach(async () => {
    Object.defineProperties(HTMLDialogElement.prototype, {
      showModal: {
        configurable: true,
        value: function showModal(this: HTMLDialogElement): void {
          this.setAttribute('open', '');
        },
      },
      close: {
        configurable: true,
        value: function close(this: HTMLDialogElement): void {
          this.removeAttribute('open');
        },
      },
    });
    currentUser.set(null);
    await TestBed.configureTestingModule({
      imports: [MobileNavigationComponent],
      providers: [
        provideRouter([{ path: 'posts', component: MobileNavigationComponent }]),
        { provide: AuthenticationService, useValue: { logout: () => of(void 0) } },
        { provide: SessionService, useValue: { currentUser, clearSession: vi.fn() } },
      ],
    }).compileComponents();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('opens and closes the navigation drawer', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(MobileNavigationComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const menuButton = hostElement.querySelector<HTMLButtonElement>(
      'button[aria-label="Ouvrir le menu de navigation"]',
    );

    expect(hostElement.querySelector('dialog')).toBeNull();
    expect(menuButton?.querySelector('app-burger-menu-icon svg')).not.toBeNull();

    menuButton!.click();
    fixture.detectChanges();
    vi.runAllTimers();

    expect(menuButton?.getAttribute('aria-expanded')).toBe('true');
    expect(hostElement.querySelector('dialog')).not.toBeNull();

    hostElement
      .querySelector<HTMLElement>('dialog')!
      .dispatchEvent(new Event('cancel', { cancelable: true }));
    fixture.detectChanges();

    expect(menuButton?.getAttribute('aria-expanded')).toBe('false');
    hostElement.querySelector<HTMLElement>('dialog')!.dispatchEvent(new Event('animationend'));
    fixture.detectChanges();

    expect(hostElement.querySelector('dialog')).toBeNull();
  });

  it('closes when the native dialog backdrop is clicked but not when its content is clicked', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(MobileNavigationComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    hostElement
      .querySelector<HTMLButtonElement>('button[aria-label="Ouvrir le menu de navigation"]')!
      .click();
    fixture.detectChanges();
    vi.runAllTimers();

    const dialog = hostElement.querySelector<HTMLDialogElement>('dialog')!;
    dialog.querySelector('div')!.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    expect(dialog.classList).toContain('mobile-menu-enter');

    dialog.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    expect(dialog.classList).toContain('mobile-menu-leave');
  });

  it('closes when Escape is pressed from the dialog', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(MobileNavigationComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    hostElement
      .querySelector<HTMLButtonElement>('button[aria-label="Ouvrir le menu de navigation"]')!
      .click();
    fixture.detectChanges();
    vi.runAllTimers();

    const dialog = hostElement.querySelector<HTMLDialogElement>('dialog')!;
    dialog.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
    fixture.detectChanges();

    expect(dialog.classList).toContain('mobile-menu-leave');
  });

  it('places logout before the navigation links and the profile icon at the bottom of the drawer', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(MobileNavigationComponent);
    currentUser.set({
      id: 1,
      username: 'manu',
      email: 'manu@example.com',
    });
    fixture.detectChanges();

    (fixture.nativeElement as HTMLElement)
      .querySelector<HTMLButtonElement>('button[aria-label="Ouvrir le menu de navigation"]')!
      .click();
    fixture.detectChanges();
    vi.runAllTimers();

    const menu = (fixture.nativeElement as HTMLElement).querySelector('dialog')!;
    const navigationItems = Array.from(
      menu.querySelectorAll<HTMLAnchorElement | HTMLButtonElement>('a, button'),
    );
    const profileLink = menu.querySelector<HTMLAnchorElement>('a[aria-label="Mon profil"]');

    expect(navigationItems.slice(0, 3).map((item) => item.textContent?.trim())).toEqual([
      'Se déconnecter',
      'Articles',
      'Thèmes',
    ]);
    expect(profileLink?.textContent?.trim()).toBe('');
    expect(menu.querySelector('button')?.closest('nav')).toBeNull();
  });
});
