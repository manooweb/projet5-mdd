import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ButtonDirective } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { BackButtonComponent } from '../../shared/back-button/back-button.component';
import { HeaderComponent } from '../../shared/header/header.component';

@Component({
  selector: 'app-login',
  imports: [BackButtonComponent, ButtonDirective, HeaderComponent, InputText],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {}
