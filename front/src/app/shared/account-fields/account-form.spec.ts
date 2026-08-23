import { createAccountForm } from './account-form';

describe('createAccountForm', () => {
  it('creates an empty registration form that requires a compliant password', () => {
    const form = createAccountForm({ passwordRequired: true });

    expect(form.controls.username.hasError('required')).toBe(true);
    expect(form.controls.email.hasError('required')).toBe(true);
    expect(form.controls.password.hasError('required')).toBe(true);

    form.controls.password.setValue('Password1');

    expect(form.controls.password.hasError('passwordComplexity')).toBe(true);

    form.controls.password.setValue('Password1!');

    expect(form.controls.password.valid).toBe(true);
  });

  it('creates a profile form with its initial data and an optional password', () => {
    const form = createAccountForm({
      username: 'demo',
      email: 'demo@mdd.net',
      passwordRequired: false,
    });

    expect(form.getRawValue()).toEqual({ username: 'demo', email: 'demo@mdd.net', password: '' });
    expect(form.controls.password.valid).toBe(true);

    form.controls.password.setValue('Password1');

    expect(form.controls.password.hasError('passwordComplexity')).toBe(true);
  });
});
