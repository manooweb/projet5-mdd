import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApiErrorComponent } from './api-error.component';

describe('ApiErrorComponent', () => {
  let fixture: ComponentFixture<ApiErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApiErrorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ApiErrorComponent);
  });

  it('displays the error message in an accessible alert', () => {
    fixture.componentRef.setInput('error', { message: 'Identifiants incorrects.' });
    fixture.detectChanges();

    const alert = (fixture.nativeElement as HTMLElement).querySelector('[role="alert"]');

    expect(alert).not.toBeNull();
    expect(alert?.textContent?.trim()).toBe('Identifiants incorrects.');
  });
});
