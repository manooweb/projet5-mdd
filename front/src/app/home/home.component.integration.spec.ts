import { By } from '@angular/platform-browser';
import { TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
    }).compileComponents();
  });

  it('displays the onboarding message when the user starts', () => {
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => undefined);
    const fixture = TestBed.createComponent(HomeComponent);
    fixture.detectChanges();

    fixture.debugElement.query(By.css('.primary-button')).triggerEventHandler('click');

    expect(alertSpy).toHaveBeenCalledWith('Commencez par lire le README et à vous de jouer !');
  });
});
