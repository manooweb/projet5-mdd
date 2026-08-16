import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AutoFocus } from 'primeng/autofocus';
import { ButtonDirective } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { BackButtonComponent } from '../../shared/back-button/back-button.component';
import { HeaderComponent } from '../../shared/header/header.component';
import { AuthenticationService } from '../authentication.service';

const passwordComplexityValidator: ValidatorFn = (control): ValidationErrors | null =>
  /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])/.test(control.value)
    ? null
    : { passwordComplexity: true };

@Component({
  selector: 'app-register',
  imports: [
    AutoFocus,
    BackButtonComponent,
    ButtonDirective,
    HeaderComponent,
    InputText,
    Password,
    ReactiveFormsModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
  private readonly authenticationService = inject(AuthenticationService);
  private readonly router = inject(Router);

  readonly isSubmitting = signal(false);

  readonly passwordStrengthRegex = '^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=.{8,})';

  readonly registerForm = new FormGroup({
    username: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(255)],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email, Validators.maxLength(255)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(72),
        passwordComplexityValidator,
      ],
    }),
  });

  submit(): void {
    if (this.registerForm.invalid || this.isSubmitting()) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.authenticationService.register(this.registerForm.getRawValue()).subscribe({
      next: () => void this.router.navigateByUrl('/posts'),
      error: () => this.isSubmitting.set(false),
    });
  }
}
