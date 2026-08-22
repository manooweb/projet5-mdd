import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { NavigationLinksComponent } from './navigation-links.component';

describe('NavigationLinksComponent', () => {
  it('renders the shared links and emits when one is selected', async () => {
    await TestBed.configureTestingModule({
      imports: [NavigationLinksComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    const fixture = TestBed.createComponent(NavigationLinksComponent);
    const navigationRequested = vi.fn();
    fixture.componentInstance.navigationRequested.subscribe(navigationRequested);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const articlesLink = hostElement.querySelector<HTMLAnchorElement>('a[aria-label="Articles"]');
    const topicsLink = hostElement.querySelector<HTMLAnchorElement>('a[aria-label="Thèmes"]');
    const profileLink = hostElement.querySelector<HTMLAnchorElement>('a[aria-label="Mon profil"]');

    expect(articlesLink?.getAttribute('href')).toBe('/posts');
    expect(topicsLink?.getAttribute('href')).toBe('/topics');
    expect(profileLink?.getAttribute('href')).toBe('/profile');

    fixture.componentInstance.onNavigationRequested();

    expect(navigationRequested).toHaveBeenCalledOnce();
  });

  it('uses the active profile icon on the profile route', async () => {
    await TestBed.configureTestingModule({
      imports: [NavigationLinksComponent],
      providers: [provideRouter([{ path: 'profile', component: NavigationLinksComponent }])],
    }).compileComponents();

    const fixture = TestBed.createComponent(NavigationLinksComponent);
    const router = TestBed.inject(Router);
    fixture.detectChanges();

    await router.navigateByUrl('/profile');
    fixture.detectChanges();

    const profileIcon = (fixture.nativeElement as HTMLElement).querySelector<SVGElement>(
      'a[aria-label="Mon profil"] svg',
    );

    expect(profileIcon?.classList).toContain('profile-icon-active');
  });
});
