import { ChangeDetectionStrategy, Component, effect, input, signal } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-form-validation-feedback',
  templateUrl: './form-validation-feedback.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FormValidationFeedbackComponent {
  readonly control = input.required<AbstractControl>();
  readonly feedbackId = input.required<string>();
  readonly message = input.required<string>();
  readonly visible = signal(false);

  private readonly syncVisibility = effect((onCleanup) => {
    const control = this.control();
    const updateVisibility = () =>
      this.visible.set(control.invalid && (control.dirty || control.touched));
    updateVisibility();

    const subscription = control.events.subscribe(updateVisibility);
    onCleanup(() => subscription.unsubscribe());
  });
}
