import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { AutoFocus } from 'primeng/autofocus';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { FormValidationFeedbackComponent } from '../form-validation-feedback/form-validation-feedback.component';
import { passwordStrengthRegex } from '../validators/password-complexity.validator';
import { AccountForm } from './account-form';

@Component({
  selector: 'app-account-fields',
  imports: [AutoFocus, FormValidationFeedbackComponent, InputText, Password, ReactiveFormsModule],
  templateUrl: './account-fields.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountFieldsComponent {
  readonly form = input.required<AccountForm>();
  readonly autofocusUsername = input(false);

  readonly passwordStrengthRegex = passwordStrengthRegex;
}
