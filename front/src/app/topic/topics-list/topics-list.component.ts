import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ProgressSpinner } from 'primeng/progressspinner';
import { HeaderComponent } from '../../shared/header/header.component';
import { Topic } from '../models/topic';
import { TopicService } from '../topic.service';

@Component({
  selector: 'app-topics-list',
  imports: [AsyncPipe, HeaderComponent, ProgressSpinner],
  templateUrl: './topics-list.component.html',
  styleUrl: './topics-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicsListComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly topicService = inject(TopicService);

  topics$ = this.topicService.getTopics();

  subscribe(topic: Topic): void {
    this.topicService
      .subscribe(topic.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.topics$ = this.topicService.getTopics();
        },
      });
  }
}
