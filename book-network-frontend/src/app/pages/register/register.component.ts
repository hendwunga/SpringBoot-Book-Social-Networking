import { Component } from '@angular/core';
import { Router } from '@angular/router';
// Auto-generated model — request body { firstname, lastname, email, password }
import {RegistrationRequest} from '../../services/models/registration-request';
// Auto-generated service — panggil POST /api/v1/auth/register
import {AuthenticationService} from '../../services/services/authentication.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  registerRequest: RegistrationRequest ={email:'',firstname:'',lastname:'',password:''}; // Form model
  errorMsg: Array<string> = []; // Pesan error validasi dari backend

constructor(
  private router: Router,
  private authService: AuthenticationService // Generated: panggil backend
  ){}

  login(){
  this.router.navigate(['login']); // Kembali ke halaman login
    }

  register(){
    this.errorMsg=[];
    // Panggil POST /api/v1/auth/register
    // Backend: AuthenticationController.register() → AuthServiceImpl.register()
    this.authService.register({
      body: this.registerRequest // { firstname, lastname, email, password }
      }).subscribe({
        next: () =>{
          // Registrasi berhasil → redirect ke halaman aktifasi akun
          this.router.navigate(['activate-account']);
          },
        error: (err) =>{
          // Backend return error validasi via GlobalExceptionHandler
          this.errorMsg = err.error.validationError; // Set<String> error messages
          }
        })
    }
}
