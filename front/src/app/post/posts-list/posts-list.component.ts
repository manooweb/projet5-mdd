import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, signal } from '@angular/core';
import { ProgressSpinner } from 'primeng/progressspinner';
import { HeaderComponent } from '../../shared/header/header.component';

@Component({
  selector: 'app-posts-list',
  imports: [HeaderComponent, ProgressSpinner],
  templateUrl: './posts-list.component.html',
  styleUrl: './posts-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostsListComponent implements OnInit, OnDestroy {
  readonly loading = signal(true);

  private loadingTimer?: ReturnType<typeof setTimeout>;

  ngOnInit(): void {
    this.loadingTimer = setTimeout(() => this.loading.set(false), 1200);
  }

  ngOnDestroy(): void {
    clearTimeout(this.loadingTimer);
  }
}
