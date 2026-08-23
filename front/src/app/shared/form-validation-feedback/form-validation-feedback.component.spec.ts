import { Component } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { TestBed } from '@angular/core/testing';
import { FormValidationFeedbackComponent } from './form-validation-feedback.component';

@Component({
  imports: [FormValidationFeedbackComponent, ReactiveFormsModule],
  template: `
    <input [formControl]="control" />
    <app-form-validation-feedback
      [control]="control"
      feedbackId="test-feedback"
      message="Ce champ est obligatoire."
    />
  `,
})
class TestHostComponent {
  readonly control = new FormControl('', { nonNullable: true, validators: [Validators.required] });
}

describe('FormValidationFeedbackComponent', () => {
  it('displays its message only after an invalid control is touched', async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent],
    }).compileComponents();

    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const feedback = hostElement.querySelector<HTMLElement>('#test-feedback');
    expect(getComputedStyle(feedback!).display).toBe('none');

    fixture.componentInstance.control.markAsTouched();
    fixture.detectChanges();

    expect(getComputedStyle(feedback!).display).toBe('block');
    expect(feedback?.textContent?.trim()).toBe('Ce champ est obligatoire.');
  });
});
