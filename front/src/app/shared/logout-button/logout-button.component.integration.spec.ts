import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { SessionService } from '../../auth/session.service';
import { LogoutButtonComponent } from './logout-button.component';

describe('LogoutButtonComponent integration', () => {
  let fixture: ComponentFixture<LogoutButtonComponent>;
  let hostElement: HTMLElement;
  let httpTesting: HttpTestingController;
  let router: Router;
  let sessionService: SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogoutButtonComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([{ path: 'login', component: LogoutButtonComponent }]),
      ],
    }).compileComponents();

    sessionService = TestBed.inject(SessionService);
    sessionService.currentUser.set({ id: 1, username: 'manu', email: 'manu@example.com' });
    fixture = TestBed.createComponent(LogoutButtonComponent);
    hostElement = fixture.nativeElement as HTMLElement;
    httpTesting = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => httpTesting.verify());

  it('initializes CSRF, clears the session, and redirects after a successful logout', () => {
    const navigateByUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('button')!.click();
    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });
    const request = httpTesting.expectOne('/api/auth/logout');
    expect(request.request.method).toBe('POST');
    request.flush(null, { status: 204, statusText: 'No Content' });

    expect(sessionService.currentUser()).toBeNull();
    expect(navigateByUrl).toHaveBeenCalledWith('/login');
  });

  it('allows another attempt when the logout API rejects the request', () => {
    fixture.detectChanges();

    hostElement.querySelector<HTMLButtonElement>('button')!.click();
    httpTesting.expectOne('/api/auth/csrf').flush(null, { status: 204, statusText: 'No Content' });
    httpTesting
      .expectOne('/api/auth/logout')
      .flush(null, { status: 500, statusText: 'Server Error' });
    fixture.detectChanges();

    expect(fixture.componentInstance.isLoggingOut()).toBe(false);
    expect(sessionService.currentUser()).not.toBeNull();
  });

  it('ignores duplicate clicks while the CSRF initialization is pending', () => {
    fixture.detectChanges();
    const button = hostElement.querySelector<HTMLButtonElement>('button')!;

    button.click();
    fixture.detectChanges();
    expect(button.disabled).toBe(true);
    button.click();

    const csrf = httpTesting.expectOne('/api/auth/csrf');
    csrf.flush(null, { status: 204, statusText: 'No Content' });
    httpTesting
      .expectOne('/api/auth/logout')
      .flush(null, { status: 204, statusText: 'No Content' });
    httpTesting.expectNone('/api/auth/csrf');
  });
});
