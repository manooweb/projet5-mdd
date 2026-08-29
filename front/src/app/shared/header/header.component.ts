import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SessionService } from '../../auth/session.service';
import { LogoutButtonComponent } from '../logout-button/logout-button.component';
import { MobileNavigationComponent } from '../mobile-navigation/mobile-navigation.component';
import { NavigationLinksComponent } from '../navigation-links/navigation-links.component';

@Component({
  selector: 'app-header',
  imports: [RouterLink, LogoutButtonComponent, MobileNavigationComponent, NavigationLinksComponent],
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  private readonly sessionService = inject(SessionService);

  readonly authentication = input(false);
  readonly currentUser = this.sessionService.currentUser;
  readonly homeLink = computed(() => (this.sessionService.currentUser() ? '/posts' : '/'));
}
