import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { Topic } from './models/topic';

@Service()
export class TopicService {
  private readonly http = inject(HttpClient);

  getTopics(): Observable<Topic[]> {
    return this.http.get<Topic[]>('/api/topics');
  }

  subscribe(topicId: number): Observable<void> {
    return this.http.post<void>(`/api/topics/${topicId}/subscription`, null);
  }

  unsubscribe(topicId: number): Observable<void> {
    return this.http.delete<void>(`/api/topics/${topicId}/subscription`);
  }
}
