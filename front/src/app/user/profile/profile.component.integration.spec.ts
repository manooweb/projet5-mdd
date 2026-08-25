import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SessionService } from '../../auth/session.service';
import { ProfileComponent } from './profile.component';

describe('ProfileComponent integration', () => {
  let fixture: ComponentFixture<ProfileComponent>;
  let hostElement: HTMLElement;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    TestBed.inject(SessionService).currentUser.set({
      id: 1,
      username: 'manu',
      email: 'manu@example.com',
    });
    fixture = TestBed.createComponent(ProfileComponent);
    hostElement = fixture.nativeElement as HTMLElement;
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('loads the profile form and displays only subscribed topics', async () => {
    fixture.detectChanges();
    flushTopics([
      { id: 1, name: 'Java', description: 'Le langage Java.', subscribed: true },
      { id: 2, name: 'Angular', description: 'Le framework Angular.', subscribed: false },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector<HTMLInputElement>('#username')?.value).toBe('manu');
    expect(hostElement.querySelectorAll('article')).toHaveLength(1);
    expect(hostElement.querySelector('article')?.textContent).toContain('Java');
  });

  it('does not send an update when no profile field has changed', async () => {
    fixture.detectChanges();
    flushTopics([]);
    await fixture.whenStable();
    fixture.detectChanges();

    hostElement.querySelector<HTMLFormElement>('form')!.dispatchEvent(new Event('submit'));

    httpTesting.expectNone('/api/users/me');
  });

  it('updates the current user through the profile API', async () => {
    fixture.detectChanges();
    flushTopics([]);
    await fixture.whenStable();
    fixture.detectChanges();

    changeUsername('manu-updated');
    hostElement.querySelector<HTMLFormElement>('form')!.dispatchEvent(new Event('submit'));

    const request = httpTesting.expectOne('/api/users/me');
    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toEqual({
      username: 'manu-updated',
      email: 'manu@example.com',
      password: '',
    });
    request.flush(null, { status: 204, statusText: 'No Content' });
    fixture.detectChanges();

    expect(TestBed.inject(SessionService).currentUser()?.username).toBe('manu-updated');
    expect(
      hostElement.querySelector<HTMLButtonElement>('form button[type="submit"]')?.disabled,
    ).toBe(true);
  });

  it('keeps the form editable and displays the API conflict message after a rejected update', async () => {
    fixture.detectChanges();
    flushTopics([]);
    await fixture.whenStable();
    fixture.detectChanges();

    changeUsername('already-used');
    hostElement.querySelector<HTMLFormElement>('form')!.dispatchEvent(new Event('submit'));
    httpTesting.expectOne('/api/users/me').flush(
      {
        status: 409,
        error: 'Conflict',
        messageCode: 'DUPLICATE_IDENTITY',
        message: 'Username or email is already used.',
        path: '/api/users/me',
      },
      { status: 409, statusText: 'Conflict' },
    );
    fixture.detectChanges();

    expect(hostElement.querySelector('app-api-error')?.textContent?.trim()).toBe(
      'Ce nom d’utilisateur ou cette adresse e-mail est déjà utilisé(e).',
    );
    expect(
      hostElement.querySelector<HTMLButtonElement>('form button[type="submit"]')?.disabled,
    ).toBe(false);
  });

  it('updates the password through the same profile endpoint', async () => {
    fixture.detectChanges();
    flushTopics([]);
    await fixture.whenStable();
    fixture.detectChanges();

    const password = hostElement.querySelector<HTMLInputElement>('#password');
    password!.value = 'Pass1!wd';
    password!.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    hostElement.querySelector<HTMLFormElement>('form')!.dispatchEvent(new Event('submit'));

    const request = httpTesting.expectOne('/api/users/me');
    expect(request.request.body).toEqual({
      username: 'manu',
      email: 'manu@example.com',
      password: 'Pass1!wd',
    });
    request.flush(null, { status: 204, statusText: 'No Content' });
  });

  it('unsubscribes then refreshes the visible subscriptions', async () => {
    fixture.detectChanges();
    flushTopics([{ id: 1, name: 'Java', description: 'Le langage Java.', subscribed: true }]);
    await fixture.whenStable();
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('article button')!.click();

    const request = httpTesting.expectOne('/api/topics/1/subscription');
    expect(request.request.method).toBe('DELETE');
    request.flush(null, { status: 204, statusText: 'No Content' });
    flushTopics([{ id: 1, name: 'Java', description: 'Le langage Java.', subscribed: false }]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(hostElement.querySelector('article')).toBeNull();
  });

  function changeUsername(value: string): void {
    const username = hostElement.querySelector<HTMLInputElement>('#username');
    username!.value = value;
    username!.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  function flushTopics(topics: unknown[]): void {
    httpTesting.expectOne('/api/topics').flush(topics);
  }
});
