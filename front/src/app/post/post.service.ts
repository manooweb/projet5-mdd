import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post } from './models/post';

export interface CreatePostRequest {
  topicId: number;
  title: string;
  content: string;
}

export type PostSortDirection = 'asc' | 'desc';

@Injectable({ providedIn: 'root' })
export class PostService {
  private readonly http = inject(HttpClient);

  getPosts(sort: PostSortDirection = 'desc'): Observable<Post[]> {
    return this.http.get<Post[]>('/api/posts', { params: { sort } });
  }

  createPost(request: CreatePostRequest): Observable<void> {
    return this.http.post<void>('/api/posts', request);
  }
}
