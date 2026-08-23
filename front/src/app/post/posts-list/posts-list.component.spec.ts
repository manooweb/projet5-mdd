import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, Subject } from 'rxjs';
import { Post } from '../models/post';
import { PostService } from '../post.service';
import { PostsListComponent } from './posts-list.component';

describe('PostsListComponent', () => {
  const postService = {
    getPosts: vi.fn<PostService['getPosts']>(() => of([])),
  };

  beforeEach(() => postService.getPosts.mockReset());

  it('provides a link to create an article without rendering a page heading', async () => {
    postService.getPosts.mockReturnValue(of([]));
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideRouter([]), { provide: PostService, useValue: postService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostsListComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const createLink = hostElement.querySelector<HTMLAnchorElement>(
      'a[aria-label="Créer un article"]',
    );

    expect(hostElement.querySelector('h1')).toBeNull();
    expect(createLink?.getAttribute('href')).toBe('/posts/create');
    expect(createLink?.textContent?.trim()).toBe('Créer un article');
    expect(hostElement.querySelector('main > div')?.classList).toContain('flex-col');
    expect(hostElement.querySelector('main > div')?.classList).toContain('sm:flex-row');
  });

  it('shows a loading spinner while the posts request is pending', async () => {
    const posts = new Subject<Post[]>();
    postService.getPosts.mockReturnValue(posts.asObservable());
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideRouter([]), { provide: PostService, useValue: postService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostsListComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('p-progress-spinner')).not.toBeNull();
  });

  it('renders the posts returned by the API', async () => {
    postService.getPosts.mockReturnValue(
      of([
        {
          id: 1,
          title: 'Un article Java',
          content: 'Son contenu.',
          author: 'demo',
          topic: 'Java',
          createdAt: '2026-08-23T10:15:30Z',
        },
      ]),
    );
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideRouter([]), { provide: PostService, useValue: postService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostsListComponent);
    fixture.detectChanges();

    const card = (fixture.nativeElement as HTMLElement).querySelector('article');
    expect(card?.textContent).toContain('Un article Java');
    expect(card?.textContent).toContain('demo');
    expect(card?.textContent).toContain('Java');
  });

  it('requests the ascending order after clicking the sort action', async () => {
    postService.getPosts.mockReturnValue(of([]));
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideRouter([]), { provide: PostService, useValue: postService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostsListComponent);
    fixture.detectChanges();

    const sortButton = (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>(
      'button[aria-label="Trier par date, ordre décroissant"]',
    );
    expect(postService.getPosts).toHaveBeenCalledWith('desc');

    sortButton!.click();
    fixture.detectChanges();

    expect(postService.getPosts).toHaveBeenLastCalledWith('asc');
  });
});
