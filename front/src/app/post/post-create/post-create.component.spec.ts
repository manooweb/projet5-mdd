import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { TopicService } from '../../topic/topic.service';
import { PostCreateComponent } from './post-create.component';

describe('PostCreateComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostCreateComponent],
      providers: [
        provideRouter([]),
        {
          provide: TopicService,
          useValue: {
            getTopics: () =>
              of([
                { id: 1, name: 'Java', description: 'Le langage Java.', subscribed: true },
                { id: 2, name: 'Angular', description: 'Le framework Angular.', subscribed: false },
              ]),
          },
        },
      ],
    }).compileComponents();
  });

  it('renders the article creation form and links its back icon to the articles list', () => {
    const fixture = TestBed.createComponent(PostCreateComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const backLink = hostElement.querySelector<HTMLAnchorElement>(
      'a[aria-label="Retour aux articles"]',
    );
    const topic = hostElement.querySelector<HTMLSelectElement>('#topicId');
    const title = hostElement.querySelector<HTMLInputElement>('#title');
    const content = hostElement.querySelector<HTMLTextAreaElement>('#content');
    const createButton = hostElement.querySelector<HTMLButtonElement>('button[type="submit"]');

    expect(hostElement.querySelector('h1')?.textContent?.trim()).toBe('Créer un nouvel article');
    expect(backLink?.getAttribute('href')).toBe('/posts');
    expect(topic?.options[0].textContent?.trim()).toBe('Sélectionner un thème');
    expect(topic?.options[1].textContent?.trim()).toBe('Java');
    expect(title).not.toBeNull();
    expect(content).not.toBeNull();
    expect(getComputedStyle(content!).resize).toBe('vertical');
    expect(createButton?.textContent?.trim()).toBe('Créer');
    expect(createButton?.disabled).toBe(true);
  });

  it('enables creation when the selected topic, title, and content are valid', () => {
    const fixture = TestBed.createComponent(PostCreateComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const topic = hostElement.querySelector<HTMLSelectElement>('#topicId');
    const title = hostElement.querySelector<HTMLInputElement>('#title');
    const content = hostElement.querySelector<HTMLTextAreaElement>('#content');

    topic!.value = '1';
    topic!.dispatchEvent(new Event('change'));
    title!.value = 'Mon article Java';
    title!.dispatchEvent(new Event('input'));
    content!.value = 'Le contenu de mon article.';
    content!.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(hostElement.querySelector<HTMLButtonElement>('button[type="submit"]')?.disabled).toBe(
      false,
    );
  });
});
