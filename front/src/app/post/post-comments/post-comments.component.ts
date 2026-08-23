import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  input,
  output,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PostComment } from '../models/post';
import { PostService } from '../post.service';

@Component({
  selector: 'app-post-comments',
  imports: [ReactiveFormsModule],
  templateUrl: './post-comments.component.html',
  styleUrl: './post-comments.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostCommentsComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly postService = inject(PostService);

  readonly comments = input<readonly PostComment[]>([]);
  readonly postId = input.required<number>();
  readonly commentCreated = output<void>();

  readonly commentForm = new FormGroup({
    content: new FormControl('', { nonNullable: true, validators: Validators.required }),
  });

  send(): void {
    if (this.commentForm.invalid) {
      this.commentForm.markAllAsTouched();
      return;
    }

    this.postService
      .createComment(this.postId(), this.commentForm.getRawValue())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.commentForm.reset();
        this.commentCreated.emit();
      });
  }
}
