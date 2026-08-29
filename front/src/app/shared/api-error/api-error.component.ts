import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ApiError } from './api-error';

@Component({
  selector: 'app-api-error',
  templateUrl: './api-error.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApiErrorComponent {
  readonly error = input.required<ApiError>();
}
