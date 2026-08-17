import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PostsListComponent } from './posts-list.component';

describe('PostsListComponent', () => {
  it('renders the articles page heading', async () => {
    await TestBed.configureTestingModule({
      imports: [PostsListComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostsListComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('h1')?.textContent?.trim()).toBe('Articles');
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
