import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonDirective } from 'primeng/button';
import { ApiError, toApiError } from '../../shared/api-error/api-error';
import { ApiErrorComponent } from '../../shared/api-error/api-error.component';
import { AccountFieldsComponent } from '../../shared/account-fields/account-fields.component';
import { createAccountForm } from '../../shared/account-fields/account-form';
import { BackButtonComponent } from '../../shared/back-button/back-button.component';
import { HeaderComponent } from '../../shared/header/header.component';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-register',
  imports: [
    AccountFieldsComponent,
    ApiErrorComponent,
    BackButtonComponent,
    ButtonDirective,
    HeaderComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './register.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
  private readonly authenticationService = inject(AuthenticationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  readonly isSubmitting = signal(false);
  readonly apiError = signal<ApiError | null>(null);

  readonly registerForm = createAccountForm({ passwordRequired: true });

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
