import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonDirective } from 'primeng/button';
import { BackButtonComponent } from '../../shared/back-button/back-button.component';
import { HeaderComponent } from '../../shared/header/header.component';
import { TopicService } from '../../topic/topic.service';
import { PostService } from '../post.service';

@Component({
  selector: 'app-post-create',
  imports: [AsyncPipe, BackButtonComponent, ButtonDirective, HeaderComponent, ReactiveFormsModule],
  templateUrl: './post-create.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostCreateComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly postService = inject(PostService);
  private readonly router = inject(Router);
  private readonly topicService = inject(TopicService);

  readonly topics$ = this.topicService.getTopics();
  readonly postForm = new FormGroup({
    topicId: new FormControl('', { nonNullable: true, validators: Validators.required }),
    title: new FormControl('', { nonNullable: true, validators: Validators.required }),
    content: new FormControl('', { nonNullable: true, validators: Validators.required }),
  });
  readonly isCreating = signal(false);

  create(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    if (this.isCreating()) {
      return;
    }

    const { topicId, title, content } = this.postForm.getRawValue();
    this.isCreating.set(true);
    this.postService
      .createPost({ topicId: Number(topicId), title, content })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => void this.router.navigateByUrl('/posts'),
        error: () => this.isCreating.set(false),
      });
  }
}
