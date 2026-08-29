import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const PASSWORD_REQUIREMENTS = [/\d/, /[a-z]/, /[A-Z]/, /[^A-Za-z0-9]/] as const;

export const passwordStrengthRegex = String.raw`^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=.{8,})`;

export const passwordComplexityValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null =>
  PASSWORD_REQUIREMENTS.every((requirement) => requirement.test(control.value))
    ? null
    : { passwordComplexity: true };

export const optionalPasswordComplexityValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null =>
  control.value.length === 0 ? null : passwordComplexityValidator(control);
