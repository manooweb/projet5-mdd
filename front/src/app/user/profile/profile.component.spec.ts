import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SessionService } from '../../auth/session.service';
import { ProfileComponent } from './profile.component';

describe('ProfileComponent', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    TestBed.inject(SessionService).currentUser.set({
      id: 1,
      username: 'demo',
      email: 'demo@mdd.net',
    });
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('renders the current user data without focusing a field by default', () => {
    const fixture = TestBed.createComponent(ProfileComponent);
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([]);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const username = hostElement.querySelector<HTMLInputElement>('#username');
    const email = hostElement.querySelector<HTMLInputElement>('#email');
    const saveButton = hostElement.querySelector<HTMLButtonElement>('form button[type="submit"]');

    expect(fixture.nativeElement.querySelector('h1')?.textContent?.trim()).toBe(
      'Profil utilisateur',
    );
    expect(username?.value).toBe('demo');
    expect(email?.value).toBe('demo@mdd.net');
    expect(document.activeElement).not.toBe(username);
    expect(saveButton?.disabled).toBe(true);
  });

  it('enables saving after a profile field is modified', () => {
    const fixture = TestBed.createComponent(ProfileComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([]);
    fixture.detectChanges();

    const username = hostElement.querySelector<HTMLInputElement>('#username');
    username!.value = 'demo-updated';
    username!.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(
      hostElement.querySelector<HTMLButtonElement>('form button[type="submit"]')?.disabled,
    ).toBe(false);
  });

  it('saves the modified profile and resets the form', () => {
    const fixture = TestBed.createComponent(ProfileComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([]);
    fixture.detectChanges();

    const username = hostElement.querySelector<HTMLInputElement>('#username');
    username!.value = 'demo-updated';
    username!.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('form button[type="submit"]')?.click();

    const request = httpTesting.expectOne('/api/users/me');
    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toEqual({
      username: 'demo-updated',
      email: 'demo@mdd.net',
      password: '',
    });
    request.flush(null);
    fixture.detectChanges();

    expect(fixture.componentInstance.profileForm.pristine).toBe(true);
    expect(TestBed.inject(SessionService).currentUser()).toEqual({
      id: 1,
      username: 'demo-updated',
      email: 'demo@mdd.net',
    });
  });

  it('displays the duplicate identity error returned by the API', () => {
    const fixture = TestBed.createComponent(ProfileComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([]);
    fixture.detectChanges();

    const username = hostElement.querySelector<HTMLInputElement>('#username');
    username!.value = 'already-used';
    username!.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('form button[type="submit"]')?.click();
    httpTesting.expectOne('/api/users/me').flush(
      {
        messageCode: 'DUPLICATE_IDENTITY',
        message: 'Username or email is already used.',
      },
      { status: 409, statusText: 'Conflict' },
    );
    fixture.detectChanges();

    expect(hostElement.querySelector('app-api-error')?.textContent?.trim()).toBe(
      'Ce nom d’utilisateur ou cette adresse e-mail est déjà utilisé(e).',
    );
  });

  it('displays the registration password validation message for an invalid new password', () => {
    const fixture = TestBed.createComponent(ProfileComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([]);
    fixture.detectChanges();

    const password = hostElement.querySelector<HTMLInputElement>('#password');
    password!.value = 'Password1';
    password!.dispatchEvent(new Event('input'));
    password!.dispatchEvent(new Event('blur'));
    fixture.detectChanges();

    const feedback = hostElement.querySelector<HTMLElement>('#password-feedback');
    expect(getComputedStyle(feedback!).display).toBe('block');
    expect(feedback?.textContent?.trim()).toBe(
      'Le mot de passe doit comporter de 8 à 72 caractères, dont un chiffre, une minuscule, une majuscule et un caractère spécial.',
    );
  });

  it('renders only subscribed topics with an unsubscribe button', async () => {
    const fixture = TestBed.createComponent(ProfileComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([
      { id: 1, name: 'Java', description: 'Le langage Java.', subscribed: true },
      { id: 2, name: 'Angular', description: 'Le framework Angular.', subscribed: false },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    const cards = hostElement.querySelectorAll('article');
    expect(cards).toHaveLength(1);
    expect(cards[0].textContent).toContain('Java');
    expect(cards[0].querySelector('button')?.textContent?.trim()).toBe('Se désabonner');
  });

  it('unsubscribes from a topic and refreshes the subscriptions list', async () => {
    const fixture = TestBed.createComponent(ProfileComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    httpTesting
      .expectOne('/api/topics')
      .flush([{ id: 1, name: 'Java', description: 'Le langage Java.', subscribed: true }]);
    await fixture.whenStable();
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('article button')?.click();

    const request = httpTesting.expectOne('/api/topics/1/subscription');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);

    httpTesting
      .expectOne('/api/topics')
      .flush([{ id: 1, name: 'Java', description: 'Le langage Java.', subscribed: false }]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector('article')).toBeNull();
  });
});
