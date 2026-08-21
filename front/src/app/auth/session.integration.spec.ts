import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideLocationMocks } from '@angular/common/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router, withDisabledInitialNavigation } from '@angular/router';
import { AppComponent } from '../app.component';
import { routes } from '../app.routes';
import { SessionService } from './session.service';

describe('session restoration integration', () => {
  let fixture: ComponentFixture<AppComponent>;
  let httpTesting: HttpTestingController;
  let location: Location;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter(routes, withDisabledInitialNavigation()),
        provideLocationMocks(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    httpTesting = TestBed.inject(HttpTestingController);
    location = TestBed.inject(Location);
    router = TestBed.inject(Router);
  });

  afterEach(() => httpTesting.verify());

  it('restores a valid session when the application starts on the articles route', async () => {
    await startApplicationOnPostsRoute();

    const currentUserRequest = httpTesting.expectOne('/api/users/me');
    currentUserRequest.flush({ id: 1, username: 'manu', email: 'manu@example.com' });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(router.url).toBe('/posts');
    expect(TestBed.inject(SessionService).currentUser()).toEqual({
      id: 1,
      username: 'manu',
      email: 'manu@example.com',
    });
    expect((fixture.nativeElement as HTMLElement).querySelector('h1')?.textContent?.trim()).toBe(
      'Articles',
    );
  });

  it('redirects to login when session restoration returns 401', async () => {
    await startApplicationOnPostsRoute();

    httpTesting.expectOne('/api/users/me').flush(null, { status: 401, statusText: 'Unauthorized' });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(router.url).toBe('/login');
    expect(TestBed.inject(SessionService).currentUser()).toBeNull();
    expect((fixture.nativeElement as HTMLElement).querySelector('h1')?.textContent?.trim()).toBe(
      'Se connecter',
    );
  });

  async function startApplicationOnPostsRoute(): Promise<void> {
    location.go('/posts');
    fixture.detectChanges();
    router.initialNavigation();
    await new Promise<void>((resolve) => setTimeout(resolve));
  }
});
