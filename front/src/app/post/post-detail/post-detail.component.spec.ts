import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PostDetailComponent } from './post-detail.component';

describe('PostDetailComponent', () => {
  it('shows the article detail loading state', async () => {
    vi.useFakeTimers();
    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostDetailComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    expect(hostElement.querySelector('h1')?.textContent?.trim()).toBe("Détail de l'article");
    expect(
      hostElement.querySelector('a[aria-label="Retour aux articles"]')?.getAttribute('href'),
    ).toBe('/posts');
    expect(hostElement.querySelector('p-progress-spinner')).not.toBeNull();
    expect(hostElement.querySelector('[aria-live="polite"]')?.textContent?.trim()).toBe(
      "Chargement de l'article...",
    );

    vi.advanceTimersByTime(1200);
    fixture.detectChanges();

    expect(hostElement.querySelector('p-progress-spinner')).toBeNull();
    expect(hostElement.querySelector('app-post-comments')).not.toBeNull();
    vi.useRealTimers();
  });
});
