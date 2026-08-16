import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('renders the registration fields and a link back to the home page', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const fields = Array.from(hostElement.querySelectorAll('input'));
    const backLink = hostElement.querySelector('app-back-button a');

    expect(fields.map((field) => field.getAttribute('name'))).toEqual([
      'username',
      'email',
      'password',
    ]);
    expect(backLink?.getAttribute('href')).toBe('/');
    expect(hostElement.querySelector('button')?.textContent?.trim()).toBe('S’inscrire');
  });
});
