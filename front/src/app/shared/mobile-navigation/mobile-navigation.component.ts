import {
  ChangeDetectionStrategy,
  Component,
  computed,
  ElementRef,
  OnDestroy,
  signal,
  viewChild,
} from '@angular/core';
import { LogoutButtonComponent } from '../logout-button/logout-button.component';
import { BurgerMenuIconComponent } from '../icons/burger-menu-icon/burger-menu-icon.component';
import { NavigationLinksComponent } from '../navigation-links/navigation-links.component';

@Component({
  selector: 'app-mobile-navigation',
  imports: [BurgerMenuIconComponent, LogoutButtonComponent, NavigationLinksComponent],
  templateUrl: './mobile-navigation.component.html',
  styleUrl: './mobile-navigation.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MobileNavigationComponent implements OnDestroy {
  private readonly menuDialog = viewChild<ElementRef<HTMLDialogElement>>('menuDialog');
  private openDialogTimer?: ReturnType<typeof setTimeout>;

  readonly menuState = signal<'closed' | 'open' | 'closing'>('closed');
  readonly isMenuRendered = computed(() => this.menuState() !== 'closed');
  readonly isMenuOpen = computed(() => this.menuState() === 'open');

  openMenu(): void {
    this.menuState.set('open');
    this.openDialogTimer = setTimeout(() => {
      const dialog = this.menuDialog()?.nativeElement;
      if (dialog && !dialog.open) {
        dialog.showModal();
      }
    }, 0);
  }

  ngOnDestroy(): void {
    clearTimeout(this.openDialogTimer);
    this.closeNativeDialog();
  }

  closeMenu(): void {
    if (this.menuState() === 'open') {
      clearTimeout(this.openDialogTimer);
      this.menuState.set('closing');
    }
  }

  onMenuAnimationEnd(): void {
    if (this.menuState() === 'closing') {
      this.closeNativeDialog();
      this.menuState.set('closed');
    }
  }

  onDialogCancel(event: Event): void {
    event.preventDefault();
    this.closeMenu();
  }

  onDialogClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.closeMenu();
    }
  }

  private closeNativeDialog(): void {
    const dialog = this.menuDialog()?.nativeElement;
    if (dialog?.open) {
      dialog.close();
    }
  }
}
