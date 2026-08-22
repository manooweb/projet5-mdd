import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../auth/authentication.service';
import { SessionService } from '../../auth/session.service';

@Component({
  selector: 'app-logout-button',
  templateUrl: './logout-button.component.html',
  styleUrl: './logout-button.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LogoutButtonComponent {
  private readonly authenticationService = inject(AuthenticationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly sessionService = inject(SessionService);

  readonly currentUser = this.sessionService.currentUser;
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
