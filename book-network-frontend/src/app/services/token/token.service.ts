import { Injectable } from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly ROLES_KEY = 'user_roles';
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

  set roles(roles: string[]) {
    localStorage.setItem(this.ROLES_KEY, JSON.stringify(roles));
  }

  get roles(): string[] {
    const roles = localStorage.getItem(this.ROLES_KEY);
    return roles ? JSON.parse(roles) : [];
  }

  get isAdmin(): boolean {
    return this.roles.includes('ADMIN');
  }

  get isUser(): boolean {
    return this.roles.includes('USER');
  }

  isTokenNotValid(): boolean {
    const token = this.token;
    return !token || this.jwtHelper.isTokenExpired(token);
  }

  clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.ROLES_KEY);
  }
}
