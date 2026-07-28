import {Component} from '@angular/core';
import {Router} from '@angular/router';
// Auto-generated service — panggil GET /api/v1/auth/activate-account?token=xxx
import {AuthenticationService} from '../../services/services/authentication.service';


@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent {

  message = '';       // Pesan sukses/gagal
  isOkay = true;      // Status aktivasi
  submitted = false;  // Apakah kode sudah dikirim?

  constructor(
    private router: Router,
    private authService: AuthenticationService // Generated: panggil backend
  ) {}

  // Panggil backend untuk aktifasi akun
  private confirmAccount(token: string) {
    // Panggil GET /api/v1/auth/activate-account?token=123456
    // Backend: AuthenticationController.confirm() → AuthServiceImpl.activateAccount()
    this.authService.confirm({
      token // Kode OTP 6 digit dari email
    }).subscribe({
      next: () => {
        // Aktifasi berhasil → tampilkan pesan sukses
        this.message = 'Your account has been successfully activated.\nNow you can proceed to login';
        this.submitted = true;
      },
      error: () => {
        // Token expired atau invalid
        this.message = 'Token has been expired or invalid';
        this.submitted = true;
        this.isOkay = false;
      }
    });
  }

  redirectToLogin() {
    this.router.navigate(['login']); // Kembali ke login
  }

  // Dipanggil oleh komponen OTP input saat user selesai input 6 digit
  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }
}
