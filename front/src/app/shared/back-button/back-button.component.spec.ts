import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { BackButtonComponent } from './back-button.component';

describe('BackButtonComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackButtonComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('links to its configured destination', () => {
    const fixture = TestBed.createComponent(BackButtonComponent);
    fixture.componentRef.setInput('target', '/register');
    fixture.componentRef.setInput('label', 'Retour à l’inscription');
    fixture.detectChanges();

    const link = (fixture.nativeElement as HTMLElement).querySelector('a');

    expect(link?.getAttribute('href')).toBe('/register');
    expect(link?.getAttribute('aria-label')).toBe('Retour à l’inscription');
  });
});
