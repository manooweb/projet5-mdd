import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { getInput } from '../../../testing/dom';
import { AuthenticationService } from '../authentication.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  const authenticationService = {
    register: vi.fn(() => of(void 0)),
  };

  beforeEach(async () => {
    authenticationService.register.mockClear();
    authenticationService.register.mockReturnValue(of(void 0));
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideRouter([]),
        { provide: AuthenticationService, useValue: authenticationService },
      ],
    }).compileComponents();
  });

  it('renders the registration fields and a link back to the home page', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const backLink = hostElement.querySelector('app-back-button a');

    expect(getInput(hostElement, 'username').name).toBe('username');
    expect(getInput(hostElement, 'email').name).toBe('email');
    expect(getInput(hostElement, 'password').name).toBe('password');
    expect(backLink?.getAttribute('href')).toBe('/');
    expect(hostElement.querySelector('button')?.textContent?.trim()).toBe('S’inscrire');
  });

  it('focuses the username field when the page is displayed', async () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    fixture.detectChanges();

    await new Promise((resolve) => setTimeout(resolve));

    expect(document.activeElement).toBe(getInput(fixture.nativeElement, 'username'));
  });

  it('keeps submission disabled until all fields are valid', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const usernameInput = getInput(hostElement, 'username');
    const emailInput = getInput(hostElement, 'email');
    const passwordInput = getInput(hostElement, 'password');
    const submitButton = hostElement.querySelector('button') as HTMLButtonElement;

    expect(submitButton.disabled).toBe(true);

    usernameInput.value = 'manu';
    usernameInput.dispatchEvent(new Event('input'));
    emailInput.value = 'manu@example.com';
    emailInput.dispatchEvent(new Event('input'));
    passwordInput.value = 'Password1';
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(submitButton.disabled).toBe(true);

    passwordInput.value = 'Password1!';
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(submitButton.disabled).toBe(false);
  });

  it('renders the PrimeNG password strength indicator, mask toggle, and validation feedback', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const feedbacks = Array.from(hostElement.querySelectorAll('.invalid-feedback'));
    const password = hostElement.querySelector('p-password');

    expect(password).not.toBeNull();
    expect(password?.getAttribute('strongRegex')).toBeNull();
    expect(password?.querySelector('#password')).not.toBeNull();
    expect(password?.querySelector('.p-password-unmask-icon')).not.toBeNull();
    expect(feedbacks).toHaveLength(3);
    expect(feedbacks[0].textContent?.trim()).toBe('Le nom d’utilisateur est obligatoire.');
    expect(feedbacks[1].textContent?.trim()).toBe('Une adresse e-mail valide est obligatoire.');
    expect(feedbacks[2].textContent?.trim()).toBe(
      'Le mot de passe doit comporter de 8 à 72 caractères, dont un chiffre, une minuscule, une majuscule et un caractère spécial.',
    );
  });

  it('toggles the password field visibility', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const passwordInput = getInput(hostElement, 'password');
    const toggle = hostElement.querySelector<HTMLElement>('.p-password-unmask-icon')!;

    expect(passwordInput.type).toBe('password');

    toggle.dispatchEvent(new MouseEvent('click'));
    fixture.detectChanges();

    expect(passwordInput.type).toBe('text');
  });

  it('rejects passwords that do not meet the functional specification', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const passwordInput = getInput(hostElement, 'password');
    const passwordControl = fixture.componentInstance.registerForm.controls.password;

    passwordInput.value = 'Password1';
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(passwordInput.closest('p-password')?.classList).toContain('ng-invalid');
    expect(getComputedStyle(hostElement.querySelector('#password-feedback')!).display).toBe(
      'block',
    );

    for (const password of ['password1!', 'PASSWORD1!', 'Password!', 'Password1', 'Pass1!']) {
      passwordControl.setValue(password);

      expect(
        passwordControl.hasError('passwordComplexity') || passwordControl.hasError('minlength'),
      ).toBe(true);
    }

    passwordControl.setValue('Password1!');

    expect(passwordControl.valid).toBe(true);

    passwordControl.setValue(`Password1!${'a'.repeat(63)}`);

    expect(passwordControl.hasError('maxlength')).toBe(true);
  });

  it('submits valid registration details and goes to the articles page on success', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    const router = TestBed.inject(Router);
    const navigateByUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    fixture.componentInstance.registerForm.setValue({
      username: 'manu',
      email: 'manu@example.com',
      password: 'Password1!',
    });
    fixture.detectChanges();

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(authenticationService.register).toHaveBeenCalledWith({
      username: 'manu',
      email: 'manu@example.com',
      password: 'Password1!',
    });
    expect(navigateByUrl).toHaveBeenCalledWith('/posts');
  });

  it('displays the API error when the registration is rejected', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    const router = TestBed.inject(Router);
    const navigateByUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    authenticationService.register.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            error: {
              status: 409,
              error: 'Conflict',
              message: 'Username or email is already used.',
              path: '/api/auth/register',
            },
          }),
      ),
    );
    fixture.componentInstance.registerForm.setValue({
      username: 'manu',
      email: 'manu@example.com',
      password: 'Password1!',
    });
    fixture.detectChanges();

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(fixture.componentInstance.isSubmitting()).toBe(false);
    expect(navigateByUrl).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('[role="alert"]')?.textContent?.trim()).toBe(
      'Username or email is already used.',
    );
  });
});
