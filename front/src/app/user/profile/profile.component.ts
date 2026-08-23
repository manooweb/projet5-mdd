import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonDirective } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { Subject, startWith, switchMap } from 'rxjs';
import { SessionService } from '../../auth/session.service';
import { HeaderComponent } from '../../shared/header/header.component';
import { TopicService } from '../../topic/topic.service';

@Component({
  selector: 'app-profile',
  imports: [AsyncPipe, ButtonDirective, HeaderComponent, InputText, Password, ReactiveFormsModule],
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

  readonly profileForm = new FormGroup({
    username: new FormControl(this.currentUser?.username ?? '', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(255)],
    }),
    email: new FormControl(this.currentUser?.email ?? '', {
      nonNullable: true,
      validators: [Validators.required, Validators.email, Validators.maxLength(255)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.minLength(8), Validators.maxLength(72)],
    }),
  });

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
}
