import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PostsListComponent } from './posts-list.component';

describe('PostsListComponent', () => {
  it('provides a link to create an article without rendering a page heading', async () => {
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideRouter([])],
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
  });

  it('shows a loading spinner temporarily', async () => {
    vi.useFakeTimers();
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostsListComponent);
    fixture.detectChanges();

    expect(fixture.componentInstance.loading()).toBe(true);
    expect(fixture.nativeElement.querySelector('p-progress-spinner')).not.toBeNull();

    vi.advanceTimersByTime(1200);
    fixture.detectChanges();

    expect(fixture.componentInstance.loading()).toBe(false);
    expect(fixture.nativeElement.querySelector('p-progress-spinner')).toBeNull();
    vi.useRealTimers();
  });
});
