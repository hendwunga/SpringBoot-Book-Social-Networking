import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../services/services/authentication.service';
import {AuthenticationRequest} from '../../services/models/authentication-request';
import {TokenService} from '../../services/token/token.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private tokenService: TokenService
  ) {
  }

  login() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        this.tokenService.token = res.accessToken as string;
        this.tokenService.refreshToken = res.refreshToken as string;
        this.tokenService.roles = res.roles as string[];
        if (res.roles?.includes('ADMIN')) {
          this.router.navigate(['books/manage-users']);
        } else {
          this.router.navigate(['books']);
        }
      },
      error: (err) => {
        console.log(err);
        if(err.error.validationError){
          this.errorMsg=err.error.validationError;
          }else{
            this.errorMsg.push(err.error.errorMsg);
          }
      }
    });
  }

  register() {
    this.router.navigate(['register']);
  }
}
