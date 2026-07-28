import {Component} from '@angular/core';
import {Router} from '@angular/router';
// Auto-generated service — panggil POST /api/v1/auth/authenticate
import {AuthenticationService} from '../../services/services/authentication.service';
// Auto-generated model — request body { email, password }
import {AuthenticationRequest} from '../../services/models/authentication-request';
// Token service — simpan tokens di localStorage
import {TokenService} from '../../services/token/token.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  authRequest: AuthenticationRequest = {email: '', password: ''}; // Form model
  errorMsg: Array<string> = []; // Pesan error dari backend

  constructor(
    private router: Router,
    private authService: AuthenticationService, // Generated: panggil backend
    private tokenService: TokenService          // Simpan tokens
  ) {
  }

  login() {
    this.errorMsg = [];
    // Panggil POST /api/v1/auth/authenticate
    // Backend: AuthenticationController.authenticate() → AuthServiceImpl.authenticate()
    this.authService.authenticate({
      body: this.authRequest // { email, password }
    }).subscribe({
      next: (res) => {
        // Simpan tokens + roles ke localStorage
        this.tokenService.token = res.accessToken as string;     // JWT access token
        this.tokenService.refreshToken = res.refreshToken as string; // JWT refresh token
        this.tokenService.roles = res.roles as string[];         // ["USER"] atau ["USER", "ADMIN"]

        // Redirect berdasarkan role
        if (res.roles?.includes('ADMIN')) {
          this.router.navigate(['books/manage-users']); // Admin → halaman manajemen user
        } else {
          this.router.navigate(['books']); // User biasa → daftar buku
        }
      },
      error: (err) => {
        console.log(err);
        // Backend return error via GlobalExceptionHandler
        if(err.error.validationError){
          this.errorMsg=err.error.validationError; // Validasi field
          }else{
            this.errorMsg.push(err.error.errorMsg); // Error umum
          }
      }
    });
  }

  register() {
    this.router.navigate(['register']); // Navigasi ke halaman registrasi
  }
}
