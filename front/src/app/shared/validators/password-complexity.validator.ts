import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const PASSWORD_COMPLEXITY = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])/;

export const passwordStrengthRegex = String.raw`^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=.{8,})`;

export const passwordComplexityValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null =>
  PASSWORD_COMPLEXITY.test(control.value) ? null : { passwordComplexity: true };

export const optionalPasswordComplexityValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null =>
  control.value.length === 0 ? null : passwordComplexityValidator(control);
