import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PostsListComponent } from './posts-list.component';

describe('PostsListComponent integration', () => {
  let fixture: ComponentFixture<PostsListComponent>;
  let hostElement: HTMLElement;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PostsListComponent);
    hostElement = fixture.nativeElement as HTMLElement;
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('shows a loading state until the default descending feed is returned', () => {
    fixture.detectChanges();

    expect(hostElement.querySelector('[aria-live="polite"]')?.textContent).toContain(
      'Chargement des articles...',
    );
    const request = httpTesting.expectOne('/api/posts?sort=desc');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('renders returned posts with their user-visible metadata', async () => {
    fixture.detectChanges();

    httpTesting.expectOne('/api/posts?sort=desc').flush([
      {
        id: 4,
        title: 'Tester une API',
        content: 'Un contenu détaillé.',
        author: 'manu',
        topic: 'Java',
        createdAt: '2026-08-23T10:15:30Z',
      },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    const card = hostElement.querySelector('article');

    expect(card?.textContent).toContain('Tester une API');
    expect(card?.textContent).toContain('manu');
    expect(card?.textContent).toContain('Java');
    expect(card?.textContent).toContain('23/08/2026');
  });

  it('links each visible post to its detail route', async () => {
    fixture.detectChanges();

    httpTesting.expectOne('/api/posts?sort=desc').flush([
      {
        id: 4,
        title: 'Tester une API',
        content: 'Un contenu détaillé.',
        author: 'manu',
        topic: 'Java',
        createdAt: '2026-08-23T10:15:30Z',
      },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(
      hostElement
        .querySelector<HTMLAnchorElement>('a[aria-label="Lire l’article Tester une API"]')
        ?.getAttribute('href'),
    ).toBe('/posts/4');
  });

  it('reloads the feed in ascending order when the user changes the sort direction', async () => {
    fixture.detectChanges();
    httpTesting.expectOne('/api/posts?sort=desc').flush([]);
    await fixture.whenStable();
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('.sort-action')!.click();
    fixture.detectChanges();

    httpTesting.expectOne('/api/posts?sort=asc').flush([]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector('.sort-action')?.getAttribute('aria-label')).toBe(
      'Trier par date, ordre croissant',
    );
  });
});
