import { AsyncPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProgressSpinner } from 'primeng/progressspinner';
import { catchError, map, of, startWith, Subject, switchMap } from 'rxjs';
import { BackButtonComponent } from '../../shared/back-button/back-button.component';
import { HeaderComponent } from '../../shared/header/header.component';
import { PostCommentsComponent } from '../post-comments/post-comments.component';
import { PostDetail } from '../models/post';
import { PostService } from '../post.service';

type PostDetailState =
  { status: 'loaded'; post: PostDetail } | { status: 'not-found' } | { status: 'error' };

@Component({
  selector: 'app-post-detail',
  imports: [
    AsyncPipe,
    BackButtonComponent,
    DatePipe,
    HeaderComponent,
    PostCommentsComponent,
    ProgressSpinner,
  ],
  templateUrl: './post-detail.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly postService = inject(PostService);
  private readonly refresh$ = new Subject<void>();

  readonly postState$ = this.route.paramMap.pipe(
    map((params) => Number(params.get('postId'))),
    switchMap((postId) =>
      this.refresh$.pipe(
        startWith(void 0),
        switchMap(() =>
          this.postService.getPost(postId).pipe(
            map((post) => ({ status: 'loaded', post }) as PostDetailState),
            catchError((error: unknown) =>
              of<PostDetailState>(
                error instanceof HttpErrorResponse && error.status === 404
                  ? { status: 'not-found' }
                  : { status: 'error' },
              ),
            ),
          ),
        ),
      ),
    ),
  );

  reloadPost(): void {
    this.refresh$.next();
  }
}
