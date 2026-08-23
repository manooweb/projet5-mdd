import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-down-arrow-icon',
  template: `
    <svg viewBox="0 0 8 17" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path
        d="M3.32833 16.3536C3.52359 16.5488 3.84018 16.5488 4.03544 16.3536L7.21742 13.1716C7.41268 12.9763 7.41268 12.6597 7.21742 12.4645C7.02216 12.2692 6.70557 12.2692 6.51031 12.4645L3.68188 15.2929L0.853458 12.4645C0.658195 12.2692 0.341613 12.2692 0.146351 12.4645C-0.0489113 12.6597 -0.0489113 12.9763 0.146351 13.1716L3.32833 16.3536ZM3.18188 0L3.18188 16H4.18188L4.18188 0L3.18188 0Z"
        fill="currentColor"
      />
    </svg>
  `,
  styleUrl: './down-arrow-icon.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DownArrowIconComponent {}
