import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-profile-icon',
  templateUrl: './profile-icon.component.html',
  styleUrl: './profile-icon.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileIconComponent {
  readonly active = input(false);
}
