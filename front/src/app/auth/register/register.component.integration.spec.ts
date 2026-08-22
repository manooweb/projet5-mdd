import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideLocationMocks } from '@angular/common/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { AppComponent } from '../../app.component';
import { routes } from '../../app.routes';

describe('RegisterComponent integration', () => {
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
    await router.navigateByUrl('/register');
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  it('displays the standardized API error returned by a rejected registration', () => {
    submitRegistration();

    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });
    httpTesting.expectOne('/api/auth/register').flush(
      {
        status: 409,
        error: 'Conflict',
        message: 'Username or email is already used.',
        path: '/api/auth/register',
      },
      { status: 409, statusText: 'Conflict' },
    );
    fixture.detectChanges();

    const apiError = (fixture.nativeElement as HTMLElement).querySelector('app-api-error');

    expect(apiError).not.toBeNull();
    expect(apiError?.textContent?.trim()).toBe('Username or email is already used.');
    expect(router.url).toBe('/register');
  });

  it('displays a fallback message when the API response does not match the error contract', () => {
    submitRegistration();

    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });
    httpTesting.expectOne('/api/auth/register').flush('Service unavailable', {
      status: 503,
      statusText: 'Service Unavailable',
    });
    fixture.detectChanges();

    const apiError = (fixture.nativeElement as HTMLElement).querySelector('app-api-error');

    expect(apiError).not.toBeNull();
    expect(apiError?.textContent?.trim()).toBe(
      'Une erreur inattendue est survenue. Veuillez réessayer.',
    );
  });

  function submitRegistration(): void {
    const hostElement = fixture.nativeElement as HTMLElement;
    const usernameInput = hostElement.querySelector<HTMLInputElement>('#username');
    const emailInput = hostElement.querySelector<HTMLInputElement>('#email');
    const passwordInput = hostElement.querySelector<HTMLInputElement>('#password');
    const form = hostElement.querySelector('form');

    usernameInput!.value = 'manu';
    usernameInput!.dispatchEvent(new Event('input'));
    emailInput!.value = 'manu@example.com';
    emailInput!.dispatchEvent(new Event('input'));
    passwordInput!.value = 'Pass1!wd';
    passwordInput!.dispatchEvent(new Event('input'));
    form!.dispatchEvent(new Event('submit'));
  }
});
