import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PostComment } from '../models/post';

@Component({
  selector: 'app-post-comments',
  imports: [ReactiveFormsModule],
  templateUrl: './post-comments.component.html',
  styleUrl: './post-comments.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostCommentsComponent {
  readonly comments = input<readonly PostComment[]>([]);

  readonly commentForm = new FormGroup({
    content: new FormControl('', { nonNullable: true, validators: Validators.required }),
  });

  send(): void {
    if (this.commentForm.invalid) {
      this.commentForm.markAllAsTouched();
    }
  }
}
