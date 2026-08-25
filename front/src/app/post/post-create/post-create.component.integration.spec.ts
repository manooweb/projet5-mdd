import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { PostCreateComponent } from './post-create.component';

describe('PostCreateComponent integration', () => {
  let fixture: ComponentFixture<PostCreateComponent>;
  let hostElement: HTMLElement;
  let httpTesting: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostCreateComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PostCreateComponent);
    hostElement = fixture.nativeElement as HTMLElement;
    httpTesting = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => httpTesting.verify());

  it('loads the available topics into the article form', async () => {
    fixture.detectChanges();

    httpTesting.expectOne('/api/topics').flush([
      { id: 1, name: 'Java', description: 'Le langage Java.', subscribed: true },
      { id: 2, name: 'Angular', description: 'Le framework Angular.', subscribed: true },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(
      Array.from(hostElement.querySelector<HTMLSelectElement>('#topicId')!.options),
    ).toHaveLength(3);
    expect(hostElement.querySelector<HTMLSelectElement>('#topicId')!.options[1].text).toBe('Java');
  });

  it('keeps an incomplete form local and does not send an article request', async () => {
    fixture.detectChanges();
    httpTesting.expectOne('/api/topics').flush([]);
    await fixture.whenStable();
    fixture.detectChanges();

    hostElement.querySelector<HTMLFormElement>('form')!.dispatchEvent(new Event('submit'));

    expect(fixture.componentInstance.postForm.touched).toBe(true);
    httpTesting.expectNone('/api/posts');
  });

  it('sends the completed article then returns to the posts list', async () => {
    const navigateByUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    fixture.detectChanges();
    httpTesting
      .expectOne('/api/topics')
      .flush([{ id: 1, name: 'Java', description: '', subscribed: true }]);
    await fixture.whenStable();
    fixture.detectChanges();

    setFormValues();
    hostElement.querySelector<HTMLFormElement>('form')!.dispatchEvent(new Event('submit'));

    const request = httpTesting.expectOne('/api/posts');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      topicId: 1,
      title: 'Tester les parcours',
      content: 'Un article créé depuis le formulaire.',
    });
    request.flush(null, { status: 201, statusText: 'Created' });

    expect(navigateByUrl).toHaveBeenCalledWith('/posts');
  });

  it('re-enables submission after a rejected article creation', async () => {
    fixture.detectChanges();
    httpTesting
      .expectOne('/api/topics')
      .flush([{ id: 1, name: 'Java', description: '', subscribed: true }]);
    await fixture.whenStable();
    fixture.detectChanges();

    setFormValues();
    hostElement.querySelector<HTMLFormElement>('form')!.dispatchEvent(new Event('submit'));
    httpTesting.expectOne('/api/posts').flush(null, { status: 500, statusText: 'Server Error' });
    fixture.detectChanges();

    expect(fixture.componentInstance.isCreating()).toBe(false);
    expect(hostElement.querySelector<HTMLButtonElement>('button[type="submit"]')?.disabled).toBe(
      false,
    );
  });

  function setFormValues(): void {
    fixture.componentInstance.postForm.setValue({
      topicId: '1',
      title: 'Tester les parcours',
      content: 'Un article créé depuis le formulaire.',
    });
    fixture.detectChanges();
  }
});
