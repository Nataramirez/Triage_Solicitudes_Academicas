import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { RegisterFormComponent } from '../../components/organisms/register-form/register-form.component';
import { UserRegisterRequest } from '../../core/shared/models/auth.model';
import { AuthService } from '../../core/services/auth-service.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [RegisterFormComponent, RouterLink],
  templateUrl: './register.page.html'
})
/**
 * @description Registration page that hosts the register form and handles form submission
 * @export
 * @class RegisterPage
 */
export class RegisterPage {

  private router = inject(Router);
  private auth = inject(AuthService);
  private notification = inject(NotificationService);

  loading = false;

  onSubmit(form: UserRegisterRequest) {
    this.loading = true;
    this.auth.register(form).subscribe({
      next: () => {
        this.notification.show('Usuario registrado de manera exitosa', 'success');
        this.router.navigate(['/home']);
      },
      error: (err: Error) => {
        this.loading = false;
        this.notification.show(err.message, 'error');
      },
      complete: () => { this.loading = false; }
    });
  }
}