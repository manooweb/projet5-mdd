import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideLocationMocks } from '@angular/common/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { AppComponent } from '../../app.component';
import { routes } from '../../app.routes';
import { SessionService } from '../session.service';

describe('LoginComponent integration', () => {
  let fixture: ComponentFixture<AppComponent>;
  let httpTesting: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter(routes),
        provideLocationMocks(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    httpTesting = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    await router.navigateByUrl('/login');
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  it('opens a session after login and closes it from the articles page', async () => {
    const hostElement = fixture.nativeElement as HTMLElement;
    const loginInput = hostElement.querySelector<HTMLInputElement>('#login');
    const passwordInput = hostElement.querySelector<HTMLInputElement>('#password');
    const form = hostElement.querySelector('form');

    expect(loginInput).not.toBeNull();
    expect(passwordInput).not.toBeNull();
    expect(form).not.toBeNull();

    loginInput!.value = 'manu@example.com';
    loginInput!.dispatchEvent(new Event('input'));
    passwordInput!.value = 'Pass1!wd';
    passwordInput!.dispatchEvent(new Event('input'));
    form!.dispatchEvent(new Event('submit'));

    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });

    const loginRequest = httpTesting.expectOne('/api/auth/login');
    expect(loginRequest.request.method).toBe('POST');
    expect(loginRequest.request.body).toEqual({ login: 'manu@example.com', password: 'Pass1!wd' });
    loginRequest.flush(null, { status: 204, statusText: 'No Content' });

    await new Promise<void>((resolve) => setTimeout(resolve));

    const currentUserRequest = httpTesting.expectOne('/api/users/me');
    currentUserRequest.flush({ id: 1, username: 'manu', email: 'manu@example.com' });

    await fixture.whenStable();
    fixture.detectChanges();
    httpTesting.expectOne('/api/posts').flush([]);

    await fixture.whenStable();
    fixture.detectChanges();

    expect(TestBed.inject(SessionService).currentUser()).toEqual({
      id: 1,
      username: 'manu',
      email: 'manu@example.com',
    });
    expect(hostElement.querySelector('a[aria-label="Créer un article"]')).not.toBeNull();

    const logoutButton = hostElement.querySelector<HTMLButtonElement>(
      'button[aria-label="Se déconnecter"]',
    );
    expect(logoutButton).not.toBeNull();
    logoutButton!.click();

    const csrfRequest = httpTesting.expectOne('/api/auth/csrf');
    csrfRequest.flush(null, { status: 204, statusText: 'No Content' });
    const logoutRequest = httpTesting.expectOne('/api/auth/logout');
    expect(logoutRequest.request.method).toBe('POST');
    logoutRequest.flush(null, { status: 204, statusText: 'No Content' });

    await new Promise<void>((resolve) => setTimeout(resolve));
    fixture.detectChanges();

    expect(router.url).toBe('/login');
    expect(TestBed.inject(SessionService).currentUser()).toBeNull();
  });

  it('displays the standardized API error returned by a rejected login', () => {
    const hostElement = fixture.nativeElement as HTMLElement;
    const loginInput = hostElement.querySelector<HTMLInputElement>('#login');
    const passwordInput = hostElement.querySelector<HTMLInputElement>('#password');
    const form = hostElement.querySelector('form');

    loginInput!.value = 'manu@example.com';
    loginInput!.dispatchEvent(new Event('input'));
    passwordInput!.value = 'Pass1!wd';
    passwordInput!.dispatchEvent(new Event('input'));
    form!.dispatchEvent(new Event('submit'));

    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });
    httpTesting.expectOne('/api/auth/login').flush(
      {
        status: 401,
        error: 'Unauthorized',
        messageCode: 'INVALID_CREDENTIALS',
        message: 'Invalid credentials.',
        path: '/api/auth/login',
      },
      { status: 401, statusText: 'Unauthorized' },
    );
    fixture.detectChanges();

    const apiError = hostElement.querySelector('app-api-error');

    expect(apiError).not.toBeNull();
    expect(apiError?.textContent?.trim()).toBe('Identifiants incorrects.');
    expect(router.url).toBe('/login');
  });
});
