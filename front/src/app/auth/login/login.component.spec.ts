import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { getInput } from '../../../testing/dom';
import { AuthenticationService } from '../authentication.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  const authenticationService = {
    login: vi.fn(() => of(void 0)),
  };

  beforeEach(async () => {
    authenticationService.login.mockClear();
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        { provide: AuthenticationService, useValue: authenticationService },
      ],
    }).compileComponents();
  });

  it('renders the login fields and a link back to the home page', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const backLink = hostElement.querySelector('app-back-button a');
    const submitButton = hostElement.querySelector('button');

    expect(getInput(hostElement, 'login').name).toBe('login');
    expect(getInput(hostElement, 'password').name).toBe('password');
    expect(backLink).not.toBeNull();
    expect(backLink!.getAttribute('href')).toBe('/');
    expect(submitButton).not.toBeNull();
    expect(submitButton!.textContent?.trim()).toBe('Se connecter');
  });

  it('focuses the login field when the page is displayed', async () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();

    await new Promise((resolve) => setTimeout(resolve));

    expect(document.activeElement).toBe(getInput(fixture.nativeElement, 'login'));
  });

  it('keeps submission disabled until both required fields are valid', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const loginInput = getInput(hostElement, 'login');
    const passwordInput = getInput(hostElement, 'password');
    const submitButton = hostElement.querySelector('button') as HTMLButtonElement;

    expect(submitButton.disabled).toBe(true);

    loginInput.value = 'manu@example.com';
    loginInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(submitButton.disabled).toBe(true);

    passwordInput.value = '1234567';
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(submitButton.disabled).toBe(true);

    passwordInput.value = '12345678';
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(submitButton.disabled).toBe(false);
  });

  it('renders CSS-driven feedback associated with each invalid field', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const loginInput = getInput(hostElement, 'login');
    const password = hostElement.querySelector('p-password');
    const feedbacks = Array.from(hostElement.querySelectorAll('.invalid-feedback'));

    expect(feedbacks).toHaveLength(2);
    expect(loginInput.getAttribute('aria-describedby')).toBe('login-feedback');
    expect(password?.getAttribute('aria-describedby')).toBe('password-feedback');
    expect(feedbacks[0].textContent?.trim()).toBe('Ce champ est obligatoire.');
    expect(feedbacks[1].textContent?.trim()).toBe(
      'Le mot de passe doit comporter au moins 8 caractères.',
    );
  });

  it('marks a password shorter than eight characters as invalid', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const passwordInput = getInput(hostElement, 'password');
    const password = hostElement.querySelector('p-password');

    passwordInput.value = '1234567';
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(fixture.componentInstance.loginForm.controls.password.hasError('minlength')).toBe(true);
    expect(password?.classList).toContain('ng-invalid');
    expect(password?.classList).toContain('ng-dirty');
    expect(getComputedStyle(hostElement.querySelector('#password-feedback')!).display).toBe(
      'block',
    );
  });

  it('submits valid credentials and goes to the articles page on success', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const router = TestBed.inject(Router);
    const navigateByUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    fixture.componentInstance.loginForm.setValue({
      login: 'manu@example.com',
      password: 'Password1!',
    });
    fixture.detectChanges();

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(authenticationService.login).toHaveBeenCalledWith({
      login: 'manu@example.com',
      password: 'Password1!',
    });
    expect(navigateByUrl).toHaveBeenCalledWith('/posts');
  });
});
