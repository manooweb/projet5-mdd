import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('renders the login fields and a link back to the home page', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const fields = Array.from(hostElement.querySelectorAll('input'));
    const backLink = hostElement.querySelector('app-back-button a');
    const submitButton = hostElement.querySelector('button');

    expect(fields.map((field) => field.getAttribute('name'))).toEqual(['login', 'password']);
    expect(backLink).not.toBeNull();
    expect(backLink!.getAttribute('href')).toBe('/');
    expect(submitButton).not.toBeNull();
    expect(submitButton!.textContent?.trim()).toBe('Se connecter');
  });
});
