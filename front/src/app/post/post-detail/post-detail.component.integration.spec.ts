import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { PostDetailComponent } from './post-detail.component';

describe('PostDetailComponent integration', () => {
  let fixture: ComponentFixture<PostDetailComponent>;
  let hostElement: HTMLElement;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { paramMap: of(convertToParamMap({ postId: '12' })) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    hostElement = fixture.nativeElement as HTMLElement;
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('shows a loading state before the article API responds', () => {
    fixture.detectChanges();

    expect(hostElement.querySelector('[aria-live="polite"]')?.textContent).toContain(
      "Chargement de l'article...",
    );
    httpTesting.expectOne('/api/posts/12').flush(postDetail());
  });

  it('renders the article and its comments returned by the API', async () => {
    fixture.detectChanges();
    httpTesting.expectOne('/api/posts/12').flush(postDetail());
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector('h1')?.textContent?.trim()).toBe('Tester une API');
    expect(hostElement.textContent).toContain('Un commentaire utile.');
    expect(
      hostElement.querySelector('[aria-label="Liste des commentaires"] article')?.textContent,
    ).toContain('manu');
  });

  it('shows the not-found state when the requested article is inaccessible', async () => {
    fixture.detectChanges();
    httpTesting.expectOne('/api/posts/12').flush(null, { status: 404, statusText: 'Not Found' });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector('[role="alert"]')?.textContent).toContain(
      'Article introuvable.',
    );
  });

  it('shows a generic error state for an unexpected API failure', async () => {
    fixture.detectChanges();
    httpTesting.expectOne('/api/posts/12').flush(null, { status: 500, statusText: 'Server Error' });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector('[role="alert"]')?.textContent).toContain(
      'Le chargement de l’article a échoué.',
    );
  });

  it('creates a comment then reloads the displayed article', async () => {
    fixture.detectChanges();
    httpTesting.expectOne('/api/posts/12').flush(postDetail());
    await fixture.whenStable();
    fixture.detectChanges();

    const commentInput = hostElement.querySelector<HTMLTextAreaElement>('#content');
    commentInput!.value = 'Un second commentaire.';
    commentInput!.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    hostElement
      .querySelector<HTMLFormElement>('app-post-comments form')!
      .dispatchEvent(new Event('submit'));

    const request = httpTesting.expectOne('/api/posts/12/comments');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ content: 'Un second commentaire.' });
    request.flush(null, { status: 201, statusText: 'Created' });

    httpTesting.expectOne('/api/posts/12').flush({
      ...postDetail(),
      comments: [
        ...postDetail().comments,
        { author: 'lisa', content: 'Un second commentaire.', createdAt: '2026-08-23T10:30:30Z' },
      ],
    });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.textContent).toContain('Un second commentaire.');
  });

  function postDetail() {
    return {
      id: 12,
      title: 'Tester une API',
      content: 'Le contenu complet de l’article.',
      author: 'manu',
      topic: 'Java',
      createdAt: '2026-08-23T10:15:30Z',
      comments: [
        { author: 'manu', content: 'Un commentaire utile.', createdAt: '2026-08-23T10:20:30Z' },
      ],
    };
  }
});
