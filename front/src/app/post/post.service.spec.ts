import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PostService } from './post.service';

describe('PostService', () => {
  let httpTesting: HttpTestingController;
  let service: PostService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PostService, provideHttpClient(), provideHttpClientTesting()],
    });

    httpTesting = TestBed.inject(HttpTestingController);
    service = TestBed.inject(PostService);
  });

  afterEach(() => httpTesting.verify());

  it('gets the posts newest first by default', () => {
    service.getPosts().subscribe();

    const request = httpTesting.expectOne('/api/posts?sort=desc');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('gets one post with its comments', () => {
    service.getPost(12).subscribe();

    const request = httpTesting.expectOne('/api/posts/12');
    expect(request.request.method).toBe('GET');
    request.flush({});
  });

  it('creates a comment on a post', () => {
    const comment = { content: 'Un commentaire utile.' };

    service.createComment(12, comment).subscribe();

    const request = httpTesting.expectOne('/api/posts/12/comments');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(comment);
    request.flush(null);
  });

  it('creates a post', () => {
    const post = { topicId: 1, title: 'Un article Java', content: 'Son contenu.' };

    service.createPost(post).subscribe();

    const request = httpTesting.expectOne('/api/posts');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(post);
    request.flush(null);
  });
});
