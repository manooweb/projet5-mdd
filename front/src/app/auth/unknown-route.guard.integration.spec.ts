import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideLocationMocks } from '@angular/common/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router, withDisabledInitialNavigation } from '@angular/router';
import { AppComponent } from '../app.component';
import { routes } from '../app.routes';

describe('unknown route integration', () => {
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

  it('shows the not-found page when an authenticated user navigates to an unknown route', async () => {
    await startApplicationOn('/unknown-page');
    httpTesting
      .expectOne('/api/users/me')
      .flush({ id: 1, username: 'manu', email: 'manu@example.com' });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(router.url).toBe('/unknown-page');
    expect((fixture.nativeElement as HTMLElement).querySelector('h1')?.textContent?.trim()).toBe(
      'Page introuvable',
    );
  });

  it('redirects an unauthenticated visitor from an unknown route to the home page', async () => {
    await startApplicationOn('/unknown-page');
    httpTesting.expectOne('/api/users/me').flush(null, { status: 401, statusText: 'Unauthorized' });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(router.url).toBe('/');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLAnchorElement>('a[href="/login"]')
        ?.textContent,
    ).toContain('Se connecter');
  });

  async function startApplicationOn(url: string): Promise<void> {
    location.go(url);
    fixture.detectChanges();
    router.initialNavigation();
    await new Promise<void>((resolve) => setTimeout(resolve));
  }
});
