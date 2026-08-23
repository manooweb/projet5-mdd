import { AsyncPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProgressSpinner } from 'primeng/progressspinner';
import { map, switchMap } from 'rxjs';
import { BackButtonComponent } from '../../shared/back-button/back-button.component';
import { HeaderComponent } from '../../shared/header/header.component';
import { PostCommentsComponent } from '../post-comments/post-comments.component';
import { PostService } from '../post.service';

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
  styleUrl: './post-detail.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly postService = inject(PostService);

  readonly post$ = this.route.paramMap.pipe(
    map((params) => Number(params.get('postId'))),
    switchMap((postId) => this.postService.getPost(postId)),
  );
}
