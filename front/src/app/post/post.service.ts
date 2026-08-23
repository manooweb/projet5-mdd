import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post, PostDetail } from './models/post';

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

  getPost(postId: number): Observable<PostDetail> {
    return this.http.get<PostDetail>(`/api/posts/${postId}`);
  }

  createPost(request: CreatePostRequest): Observable<void> {
    return this.http.post<void>('/api/posts', request);
  }
}
