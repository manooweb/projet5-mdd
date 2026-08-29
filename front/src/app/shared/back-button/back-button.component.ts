import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ArrowLeft } from '@primeicons/angular/arrow-left';

@Component({
  selector: 'app-back-button',
  imports: [ArrowLeft, RouterLink],
  templateUrl: './back-button.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BackButtonComponent {
  readonly target = input('/');
  readonly label = input('Retour');
}
