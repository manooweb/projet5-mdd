import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AuthenticationService } from '../../auth/authentication.service';
import { CurrentUser } from '../../auth/models/current-user';
import { SessionService } from '../../auth/session.service';
import { MobileNavigationComponent } from './mobile-navigation.component';

describe('MobileNavigationComponent integration', () => {
  const currentUser = signal<CurrentUser | null>(null);
  let fixture: ComponentFixture<MobileNavigationComponent>;
  let hostElement: HTMLElement;

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
        provideRouter([
          { path: 'posts', component: MobileNavigationComponent },
          { path: 'topics', component: MobileNavigationComponent },
          { path: 'profile', component: MobileNavigationComponent },
        ]),
        { provide: AuthenticationService, useValue: { logout: () => of(void 0) } },
        { provide: SessionService, useValue: { currentUser, clearSession: vi.fn() } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MobileNavigationComponent);
    hostElement = fixture.nativeElement as HTMLElement;
  });

  afterEach(() => vi.useRealTimers());

  it('does not render the drawer before the user opens it', () => {
    fixture.detectChanges();

    expect(hostElement.querySelector('dialog')).toBeNull();
    expect(
      hostElement
        .querySelector<HTMLButtonElement>('button[aria-label="Ouvrir le menu de navigation"]')
        ?.getAttribute('aria-expanded'),
    ).toBe('false');
  });

  it('opens the native dialog and exposes the navigation links', () => {
    openMenu();

    const dialog = hostElement.querySelector<HTMLDialogElement>('dialog');

    expect(dialog?.open).toBe(true);
    expect(
      Array.from(dialog!.querySelectorAll('a')).map((link) => link.textContent?.trim()),
    ).toEqual(['Articles', 'Thèmes', '']);
    expect(dialog?.querySelector('a[aria-label="Mon profil"]')).not.toBeNull();
  });

  it('closes the drawer when the native cancel event is emitted', () => {
    openMenu();

    hostElement
      .querySelector<HTMLDialogElement>('dialog')!
      .dispatchEvent(new Event('cancel', { cancelable: true }));
    fixture.detectChanges();

    expect(hostElement.querySelector('dialog')?.classList).toContain('mobile-menu-leave');
  });

  it('closes only when the user clicks the dialog backdrop', () => {
    openMenu();
    const dialog = hostElement.querySelector<HTMLDialogElement>('dialog')!;

    dialog.querySelector('div')!.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();
    expect(dialog.classList).toContain('mobile-menu-enter');

    dialog.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();
    expect(dialog.classList).toContain('mobile-menu-leave');
  });

  it('closes on Escape and removes the dialog after its leave animation', () => {
    openMenu();
    const dialog = hostElement.querySelector<HTMLDialogElement>('dialog')!;

    dialog.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
    fixture.detectChanges();
    dialog.dispatchEvent(new Event('animationend'));
    fixture.detectChanges();

    expect(hostElement.querySelector('dialog')).toBeNull();
  });

  it('starts closing when a navigation link is selected from the drawer', () => {
    openMenu();

    hostElement.querySelector<HTMLAnchorElement>('a[aria-label="Articles"]')!.click();
    fixture.detectChanges();

    expect(hostElement.querySelector('dialog')?.classList).toContain('mobile-menu-leave');
  });

  it('removes the drawer after navigation has triggered its leave animation', () => {
    openMenu();

    hostElement.querySelector<HTMLAnchorElement>('a[aria-label="Mon profil"]')!.click();
    fixture.detectChanges();
    hostElement.querySelector('dialog')!.dispatchEvent(new Event('animationend'));
    fixture.detectChanges();

    expect(hostElement.querySelector('dialog')).toBeNull();
  });

  function openMenu(): void {
    vi.useFakeTimers();
    fixture.detectChanges();
    hostElement
      .querySelector<HTMLButtonElement>('button[aria-label="Ouvrir le menu de navigation"]')!
      .click();
    fixture.detectChanges();
    vi.runAllTimers();
    fixture.detectChanges();
  }
});
