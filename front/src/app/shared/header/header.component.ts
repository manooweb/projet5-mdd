import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  input,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { Bars } from '@primeicons/angular/bars';
import { AuthenticationService } from '../../auth/authentication.service';
import { SessionService } from '../../auth/session.service';

@Component({
  selector: 'app-header',
  imports: [Bars, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  private readonly authenticationService = inject(AuthenticationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly sessionService = inject(SessionService);

  readonly authentication = input(false);
  readonly currentUser = this.sessionService.currentUser;
  readonly homeLink = computed(() => (this.sessionService.currentUser() ? '/posts' : '/'));
  readonly isLoggingOut = signal(false);

  logout(): void {
    if (this.isLoggingOut()) {
      return;
    }

    this.isLoggingOut.set(true);
    this.authenticationService
      .logout()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.sessionService.clearSession();
          void this.router.navigateByUrl('/login');
        },
        error: () => this.isLoggingOut.set(false),
      });
  }
}
