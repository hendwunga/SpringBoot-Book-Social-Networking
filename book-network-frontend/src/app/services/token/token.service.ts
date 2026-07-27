import { Injectable } from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private jwtHelper = new JwtHelperService();

  set token(token: string) {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
  }

  get token(): string {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY) as string;
  }

  set refreshToken(refreshToken: string) {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  get refreshToken(): string {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY) as string;
  }

  isTokenNotValid(): boolean {
    const token = this.token;
    return !token || this.jwtHelper.isTokenExpired(token);
  }

  clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }
}
