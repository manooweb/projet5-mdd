import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PostDetailComponent } from './post-detail.component';

describe('PostDetailComponent', () => {
  it('shows the article detail loading state', async () => {
    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostDetailComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    expect(hostElement.querySelector('h1')?.textContent?.trim()).toBe("Détail de l'article");
    expect(hostElement.querySelector('p-progress-spinner')).not.toBeNull();
    expect(hostElement.querySelector('[aria-live="polite"]')?.textContent?.trim()).toBe(
      "Chargement de l'article...",
    );
  });
});
