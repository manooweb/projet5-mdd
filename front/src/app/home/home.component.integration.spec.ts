import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { routes } from '../app.routes';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [provideRouter(routes)],
    }).compileComponents();
  });

  it('offers routes to sign in and register', () => {
    const fixture = TestBed.createComponent(HomeComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const links = Array.from(hostElement.querySelectorAll('a'));

    expect(links.map((link) => link.getAttribute('href'))).toEqual(['/login', '/register']);
    expect(links.map((link) => link.textContent?.trim())).toEqual(['Se connecter', 'S’inscrire']);
  });
});
