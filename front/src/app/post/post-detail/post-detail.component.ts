import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ProgressSpinner } from 'primeng/progressspinner';
import { map, startWith, timer } from 'rxjs';
import { BackButtonComponent } from '../../shared/back-button/back-button.component';
import { HeaderComponent } from '../../shared/header/header.component';
import { PostCommentsComponent } from '../post-comments/post-comments.component';

@Component({
  selector: 'app-post-detail',
  imports: [
    AsyncPipe,
    BackButtonComponent,
    HeaderComponent,
    PostCommentsComponent,
    ProgressSpinner,
  ],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetailComponent {
  readonly loading$ = timer(1200).pipe(
    map(() => false),
    startWith(true),
  );
}
