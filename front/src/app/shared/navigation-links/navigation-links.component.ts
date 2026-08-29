import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { ProfileIconComponent } from '../icons/profile-icon/profile-icon.component';

@Component({
  selector: 'app-navigation-links',
  imports: [ProfileIconComponent, RouterLink, RouterLinkActive],
  templateUrl: './navigation-links.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavigationLinksComponent {
  readonly layout = input<'desktop' | 'mobile'>('desktop');
  readonly navigationRequested = output<void>();

  onNavigationRequested(): void {
    this.navigationRequested.emit();
  }
}
