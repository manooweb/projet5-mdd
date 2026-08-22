import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Topic } from './models/topic';

@Injectable({ providedIn: 'root' })
export class TopicService {
  private readonly http = inject(HttpClient);

  getTopics(): Observable<Topic[]> {
    return this.http.get<Topic[]>('/api/topics');
  }
}
