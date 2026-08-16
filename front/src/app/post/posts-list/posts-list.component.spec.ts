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
});
