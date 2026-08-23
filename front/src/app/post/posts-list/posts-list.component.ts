import { AsyncPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProgressSpinner } from 'primeng/progressspinner';
import { DownArrowIconComponent } from '../../shared/down-arrow-icon/down-arrow-icon.component';
import { HeaderComponent } from '../../shared/header/header.component';
import { PostService, PostSortDirection } from '../post.service';

@Component({
  selector: 'app-posts-list',
  imports: [
    AsyncPipe,
    DatePipe,
    DownArrowIconComponent,
    HeaderComponent,
    ProgressSpinner,
    RouterLink,
  ],
  templateUrl: './posts-list.component.html',
  styleUrl: './posts-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostsListComponent {
  private readonly postService = inject(PostService);

  readonly sortDirection = signal<PostSortDirection>('desc');
  readonly posts$ = computed(() => this.postService.getPosts(this.sortDirection()));

  toggleSort(): void {
    this.sortDirection.update((sort) => (sort === 'desc' ? 'asc' : 'desc'));
  }
}
