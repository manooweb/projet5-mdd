import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TopicsListComponent } from './topics-list.component';

describe('TopicsListComponent', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopicsListComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('loads and renders a card for every topic', async () => {
    const fixture = TestBed.createComponent(TopicsListComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    expect(hostElement.querySelector('p-progress-spinner')).not.toBeNull();

    const request = httpTesting.expectOne('/api/topics');
    expect(request.request.method).toBe('GET');
    request.flush([
      {
        id: 1,
        name: 'Java',
        description: 'Discussions autour du langage Java.',
        subscribed: false,
      },
      {
        id: 2,
        name: 'Spring Boot',
        description: 'Conception d’applications avec Spring Boot.',
        subscribed: true,
      },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    const cards = hostElement.querySelectorAll<HTMLElement>('article');
    expect(hostElement.querySelector('p-progress-spinner')).toBeNull();
    expect(cards).toHaveLength(2);
    expect(cards[0].textContent).toContain('Java');
    expect(cards[0].textContent).toContain('Discussions autour du langage Java.');
    expect(cards[0].querySelector('button')?.textContent?.trim()).toBe("S'abonner");
  });

  it('renders an inactive already subscribed button for subscribed topics', async () => {
    const fixture = TestBed.createComponent(TopicsListComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    const request = httpTesting.expectOne('/api/topics');
    request.flush([
      {
        id: 2,
        name: 'Spring Boot',
        description: 'Conception d’applications avec Spring Boot.',
        subscribed: true,
      },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    const button = hostElement.querySelector<HTMLButtonElement>('article button');
    expect(button?.textContent?.trim()).toBe('Déjà abonné');
    expect(button?.disabled).toBe(true);
  });

  it('subscribes to a topic and refreshes its subscription state', async () => {
    const fixture = TestBed.createComponent(TopicsListComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([
      {
        id: 1,
        name: 'Java',
        description: 'Discussions autour du langage Java.',
        subscribed: false,
      },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('article button')?.click();

    const request = httpTesting.expectOne('/api/topics/1/subscription');
    expect(request.request.method).toBe('POST');
    request.flush(null);

    httpTesting.expectOne('/api/topics').flush([
      {
        id: 1,
        name: 'Java',
        description: 'Discussions autour du langage Java.',
        subscribed: true,
      },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    const button = hostElement.querySelector<HTMLButtonElement>('article button');
    expect(button?.textContent?.trim()).toBe('Déjà abonné');
    expect(button?.disabled).toBe(true);
  });
});
