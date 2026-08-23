import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  optionalPasswordComplexityValidator,
  passwordComplexityValidator,
} from '../validators/password-complexity.validator';

export interface AccountFormControls {
  username: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
}

export type AccountForm = FormGroup<AccountFormControls>;

interface CreateAccountFormOptions {
  username?: string;
  email?: string;
  passwordRequired: boolean;
}

export function createAccountForm({
  username = '',
  email = '',
  passwordRequired,
}: CreateAccountFormOptions): AccountForm {
  return new FormGroup({
    username: new FormControl(username, {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(255)],
    }),
    email: new FormControl(email, {
      nonNullable: true,
      validators: [Validators.required, Validators.email, Validators.maxLength(255)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [
        ...(passwordRequired ? [Validators.required] : []),
        Validators.minLength(8),
        Validators.maxLength(72),
        passwordRequired ? passwordComplexityValidator : optionalPasswordComplexityValidator,
      ],
    }),
  });
}
