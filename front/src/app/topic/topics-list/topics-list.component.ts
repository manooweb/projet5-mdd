import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
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
export class TopicsListComponent implements OnInit {
  readonly loading = signal(true);
  readonly topics = signal<Topic[]>([]);

  private readonly topicService = inject(TopicService);

  ngOnInit(): void {
    this.topicService.getTopics().subscribe({
      next: (topics) => {
        this.topics.set(topics);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
