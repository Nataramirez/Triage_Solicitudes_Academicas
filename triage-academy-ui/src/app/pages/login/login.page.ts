import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth-service.service';
import { NotificationService } from '../../core/services/notification.service';
import { LoginFormComponent } from '../../components/organisms/login-form/login-form.component';
import { LoginRequest } from '../../core/shared/models/auth.model';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [LoginFormComponent, RouterLink],
  templateUrl: './login.page.html'
})
/**
 * @description Login page that hosts the login form and handles form submission
 * @export
 * @class LoginPage
 */
export class LoginPage {
  private auth = inject(AuthService);
  private notification = inject(NotificationService);
  private router = inject(Router);

  loading = false;

  onSubmit(form: LoginRequest) {
    this.loading = true;
    this.auth.login(form).subscribe({
      next: () => {
        this.notification.show('Bienvenido', 'success');
        this.router.navigate([this.auth.getHomeRoute()]);
      },
      error: (err: Error) => {
        this.loading = false;
        this.notification.show(err.message, 'error');
      },
      complete: () => { this.loading = false; }
    });
  }
}
