import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TopicsListComponent } from './topics-list.component';

describe('TopicsListComponent', () => {
  it('renders the topics page heading and loading spinner', async () => {
    await TestBed.configureTestingModule({
      imports: [TopicsListComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    const fixture = TestBed.createComponent(TopicsListComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('h1')?.textContent?.trim()).toBe('Thèmes');
    expect(fixture.nativeElement.querySelector('p-progress-spinner')).not.toBeNull();
  });
});
