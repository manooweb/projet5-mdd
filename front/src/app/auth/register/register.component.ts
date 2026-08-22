import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
import { ApiError, toApiError } from '../../shared/api-error/api-error';
import { ApiErrorComponent } from '../../shared/api-error/api-error.component';

const passwordComplexityValidator: ValidatorFn = (control): ValidationErrors | null =>
  /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])/.test(control.value)
    ? null
    : { passwordComplexity: true };

@Component({
  selector: 'app-register',
  imports: [
    AutoFocus,
    ApiErrorComponent,
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
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  readonly isSubmitting = signal(false);
  readonly apiError = signal<ApiError | null>(null);

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
    this.apiError.set(null);
    this.authenticationService
      .register(this.registerForm.getRawValue())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => void this.router.navigateByUrl('/posts'),
        error: (error: HttpErrorResponse) => {
          this.isSubmitting.set(false);
          this.apiError.set(toApiError(error));
        },
      });
  }
}
