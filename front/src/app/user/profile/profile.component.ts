import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonDirective } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
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
  private readonly sessionService = inject(SessionService);
  private readonly topicService = inject(TopicService);
  private readonly currentUser = this.sessionService.currentUser();

  readonly topics$ = this.topicService.getTopics();

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
}
