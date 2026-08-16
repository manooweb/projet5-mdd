import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('shows the navigation menu trigger for interior pages', () => {
    const fixture = TestBed.createComponent(HeaderComponent);
    fixture.detectChanges();

    const button = (fixture.nativeElement as HTMLElement).querySelector('button');

    expect(button?.getAttribute('aria-label')).toBe('Ouvrir le menu de navigation');
  });

  it('hides the navigation menu trigger for authentication pages', () => {
    const fixture = TestBed.createComponent(HeaderComponent);
    fixture.componentRef.setInput('authentication', true);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('button')).toBeNull();
  });
});
