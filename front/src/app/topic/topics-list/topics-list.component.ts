import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ProgressSpinner } from 'primeng/progressspinner';
import { HeaderComponent } from '../../shared/header/header.component';
import { Topic } from '../models/topic';
import { TopicService } from '../topic.service';

@Component({
  selector: 'app-topics-list',
  imports: [HeaderComponent, ProgressSpinner],
  templateUrl: './topics-list.component.html',
  styleUrl: './topics-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicsListComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly topicService = inject(TopicService);

  private readonly topicsResource = rxResource({
    defaultValue: [] as Topic[],
    params: () => true,
    stream: () => this.topicService.getTopics(),
  });

  readonly loading = this.topicsResource.isLoading;
  readonly topics = this.topicsResource.value;

  subscribe(topic: Topic): void {
    this.topicService
      .subscribe(topic.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.topicsResource.update((topics) =>
            topics.map((currentTopic) =>
              currentTopic.id === topic.id ? { ...currentTopic, subscribed: true } : currentTopic,
            ),
          );
        },
      });
  }
}
