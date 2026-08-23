import { Component, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { createAccountForm } from './account-form';
import { AccountFieldsComponent } from './account-fields.component';

@Component({
  imports: [AccountFieldsComponent, ReactiveFormsModule],
  template: `
    <form [formGroup]="form">
      <app-account-fields [form]="form" [autofocusUsername]="autofocusUsername()" />
    </form>
  `,
})
class TestHostComponent {
  readonly form = createAccountForm({ passwordRequired: true });
  readonly autofocusUsername = signal(false);
}

describe('AccountFieldsComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent],
    }).compileComponents();
  });

  it('renders the shared fields with accessible identifiers and password strength feedback', () => {
    const fixture = TestBed.createComponent(TestHostComponent);
    const hostElement = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();

    const username = hostElement.querySelector<HTMLInputElement>('#username');
    const email = hostElement.querySelector<HTMLInputElement>('#email');
    const password = hostElement.querySelector<HTMLInputElement>('#password');

    expect(username?.getAttribute('aria-describedby')).toBe('username-feedback');
    expect(email?.getAttribute('aria-describedby')).toBe('email-feedback');
    expect(password?.getAttribute('aria-describedby')).toBe('password-feedback');
    expect(hostElement.querySelector('p-password .p-password-unmask-icon')).not.toBeNull();
  });

  it('focuses the username only when requested by its parent', async () => {
    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.componentInstance.autofocusUsername.set(true);
    fixture.detectChanges();

    await new Promise((resolve) => setTimeout(resolve));

    expect(document.activeElement).toBe(
      (fixture.nativeElement as HTMLElement).querySelector('#username'),
    );
  });
});
