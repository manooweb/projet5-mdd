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
    fixture.componentRef.setInput('target', '/destination-de-test');
    fixture.componentRef.setInput('label', 'Retour de test');
    fixture.detectChanges();

    const link = (fixture.nativeElement as HTMLElement).querySelector('a');

    expect(link).not.toBeNull();
    expect(link!.getAttribute('href')).toBe('/destination-de-test');
    expect(link!.getAttribute('aria-label')).toBe('Retour de test');
  });
});
