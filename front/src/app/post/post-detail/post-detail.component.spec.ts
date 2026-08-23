import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';
import { PostDetail } from '../models/post';
import { PostService } from '../post.service';
import { PostDetailComponent } from './post-detail.component';

describe('PostDetailComponent', () => {
  it('shows the article detail loading state', async () => {
    const post = new Subject<PostDetail>();
    const postService = { getPost: vi.fn(() => post) };
    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { paramMap: of(convertToParamMap({ postId: '12' })) },
        },
        { provide: PostService, useValue: postService },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostDetailComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    expect(hostElement.querySelector('h1')).toBeNull();
    expect(hostElement.querySelector('a[aria-label="Retour aux articles"]')).toBeNull();
    expect(hostElement.querySelector('p-progress-spinner')).not.toBeNull();
    expect(hostElement.querySelector('[aria-live="polite"]')?.textContent?.trim()).toBe(
      "Chargement de l'article...",
    );

    post.next({
      id: 12,
      title: 'Article détaillé',
      content: "Le contenu complet de l'article.",
      author: 'demo',
      topic: 'Java',
      createdAt: '2026-08-23T10:15:30Z',
      comments: [
        { author: 'demo', content: 'Un commentaire utile.', createdAt: '2026-08-23T10:20:30Z' },
      ],
    });
    fixture.detectChanges();

    expect(hostElement.querySelector('p-progress-spinner')).toBeNull();
    expect(hostElement.querySelector('app-post-comments')).not.toBeNull();
    expect(hostElement.textContent).toContain('Article détaillé');
    expect(hostElement.textContent).toContain('Un commentaire utile.');
    expect(hostElement.querySelector('h1')?.textContent?.trim()).toBe('Article détaillé');

    fixture.componentInstance.reloadPost();

    expect(postService.getPost).toHaveBeenCalledTimes(2);
  });

  it('shows a not found message when the post does not exist', async () => {
    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { paramMap: of(convertToParamMap({ postId: '99' })) },
        },
        {
          provide: PostService,
          useValue: {
            getPost: vi.fn(() => throwError(() => new HttpErrorResponse({ status: 404 }))),
          },
        },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostDetailComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    expect(hostElement.querySelector('p-progress-spinner')).toBeNull();
    expect(hostElement.querySelector('[role="alert"]')?.textContent).toContain(
      'Article introuvable.',
    );
    expect(
      hostElement.querySelector('a[aria-label="Retour aux articles"]')?.getAttribute('href'),
    ).toBe('/posts');
  });
});
