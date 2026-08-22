import { TestBed } from '@angular/core/testing';
import { ProfileIconComponent } from './profile-icon.component';

describe('ProfileIconComponent', () => {
  it('uses the active appearance when requested', async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileIconComponent],
    }).compileComponents();

    const fixture = TestBed.createComponent(ProfileIconComponent);
    fixture.componentRef.setInput('active', true);
    fixture.detectChanges();

    const icon = (fixture.nativeElement as HTMLElement).querySelector('svg');

    expect(icon?.classList).toContain('profile-icon-active');
  });
});
