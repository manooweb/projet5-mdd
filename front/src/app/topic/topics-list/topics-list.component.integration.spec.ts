import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TopicsListComponent } from './topics-list.component';

describe('TopicsListComponent integration', () => {
  let fixture: ComponentFixture<TopicsListComponent>;
  let hostElement: HTMLElement;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopicsListComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(TopicsListComponent);
    hostElement = fixture.nativeElement as HTMLElement;
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('shows a loading state until the topics API responds', () => {
    fixture.detectChanges();

    expect(hostElement.querySelector('[aria-live="polite"]')?.textContent).toContain(
      'Chargement des thèmes...',
    );

    httpTesting.expectOne('/api/topics').flush([]);
  });

  it('renders every topic returned by the API', async () => {
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([
      { id: 1, name: 'Java', description: 'Le langage Java.', subscribed: false },
      { id: 2, name: 'Angular', description: 'Le framework Angular.', subscribed: true },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    const cards = hostElement.querySelectorAll('article');

    expect(cards).toHaveLength(2);
    expect(cards[0].textContent).toContain('Java');
    expect(cards[1].textContent).toContain('Angular');
  });

  it('prevents a second subscription when the API reports an already followed topic', async () => {
    fixture.detectChanges();

    httpTesting
      .expectOne('/api/topics')
      .flush([{ id: 2, name: 'Angular', description: 'Le framework Angular.', subscribed: true }]);
    await fixture.whenStable();
    fixture.detectChanges();

    const button = hostElement.querySelector<HTMLButtonElement>('article button');

    expect(button?.textContent?.trim()).toBe('Déjà abonné');
    expect(button?.disabled).toBe(true);
  });

  it('subscribes then refreshes the visible topic state', async () => {
    fixture.detectChanges();

    httpTesting
      .expectOne('/api/topics')
      .flush([{ id: 1, name: 'Java', description: 'Le langage Java.', subscribed: false }]);
    await fixture.whenStable();
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('article button')!.click();

    const subscription = httpTesting.expectOne('/api/topics/1/subscription');
    expect(subscription.request.method).toBe('POST');
    subscription.flush(null);

    httpTesting
      .expectOne('/api/topics')
      .flush([{ id: 1, name: 'Java', description: 'Le langage Java.', subscribed: true }]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector<HTMLButtonElement>('article button')?.disabled).toBe(true);
  });
});
