import { AsyncPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ButtonDirective } from 'primeng/button';
import { Subject, startWith, switchMap } from 'rxjs';
import { SessionService } from '../../auth/session.service';
import { ApiError, toApiError } from '../../shared/api-error/api-error';
import { ApiErrorComponent } from '../../shared/api-error/api-error.component';
import { AccountFieldsComponent } from '../../shared/account-fields/account-fields.component';
import { createAccountForm } from '../../shared/account-fields/account-form';
import { HeaderComponent } from '../../shared/header/header.component';
import { TopicService } from '../../topic/topic.service';

@Component({
  selector: 'app-profile',
  imports: [
    AsyncPipe,
    AccountFieldsComponent,
    ApiErrorComponent,
    ButtonDirective,
    HeaderComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly sessionService = inject(SessionService);
  private readonly topicService = inject(TopicService);
  private readonly currentUser = this.sessionService.currentUser();
  private readonly refreshTopics$ = new Subject<void>();

  readonly topics$ = this.refreshTopics$.pipe(
    startWith(void 0),
    switchMap(() => this.topicService.getTopics()),
  );

  readonly profileForm = createAccountForm({
    username: this.currentUser?.username,
    email: this.currentUser?.email,
    passwordRequired: false,
  });
  readonly isSubmitting = signal(false);
  readonly apiError = signal<ApiError | null>(null);

  unsubscribe(topicId: number): void {
    this.topicService
      .unsubscribe(topicId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.refreshTopics$.next();
        },
      });
  }

  submit(): void {
    if (this.profileForm.invalid || this.isSubmitting()) {
      this.profileForm.markAllAsTouched();
      return;
    }

    if (this.profileForm.pristine) {
      return;
    }

    const details = this.profileForm.getRawValue();
    this.isSubmitting.set(true);
    this.apiError.set(null);
    this.sessionService
      .updateCurrentUser(details)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.profileForm.reset({
            username: details.username,
            email: details.email,
            password: '',
          });
          this.isSubmitting.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.isSubmitting.set(false);
          this.apiError.set(toApiError(error));
        },
      });
  }
}
