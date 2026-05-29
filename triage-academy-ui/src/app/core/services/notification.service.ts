import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

const Toast = Swal.mixin({
  toast: true,
  position: 'top-end',
  showConfirmButton: false,
  timer: 3000,
  timerProgressBar: true
});

@Injectable({ providedIn: 'root' })
export class NotificationService {

  show(message: string, icon: 'success' | 'error' | 'warning' | 'info') {
    Toast.fire({ icon, title: message });
  }
}
