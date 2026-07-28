import { Injectable } from '@angular/core';
// JwtHelperService dari @auth0/angular-jwt — decode JWT tanpa verify signature di frontend
import {JwtHelperService} from '@auth0/angular-jwt';

// TokenService — mengelola JWT tokens (access, refresh) di localStorage
// Terhubung ke backend: AuthServiceImpl.generateToken(), generateRefreshToken()
@Injectable({
  providedIn: 'root' // Singleton: satu instance di seluruh aplikasi
})
export class TokenService {

  // Key untuk simpan di localStorage
  private readonly ACCESS_TOKEN_KEY = 'access_token';  // JWT access token
  private readonly REFRESH_TOKEN_KEY = 'refresh_token'; // JWT refresh token
  private readonly ROLES_KEY = 'user_roles';            // Daftar role ["USER", "ADMIN"]
  private jwtHelper = new JwtHelperService(); // Helper decode JWT

  // ========== ACCESS TOKEN ==========
  // Simpan access token ke localStorage (dipanggil saat login berhasil)
  set token(token: string) {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
  }

  // Ambil access token dari localStorage (dipanggil oleh HttpTokenInterceptor)
  get token(): string {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY) as string;
  }

  // ========== REFRESH TOKEN ==========
  // Simpan refresh token ke localStorage
  set refreshToken(refreshToken: string) {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  // Ambil refresh token
  get refreshToken(): string {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY) as string;
  }

  // ========== ROLES ==========
  // Simpan roles ke localStorage (dipanggil saat login berhasil)
  set roles(roles: string[]) {
    localStorage.setItem(this.ROLES_KEY, JSON.stringify(roles));
  }

  // Ambil roles dari localStorage → return array string
  get roles(): string[] {
    const roles = localStorage.getItem(this.ROLES_KEY);
    return roles ? JSON.parse(roles) : [];
  }

  // Cek apakah user punya role ADMIN
  get isAdmin(): boolean {
    return this.roles.includes('ADMIN');
  }

  // Cek apakah user punya role USER
  get isUser(): boolean {
    return this.roles.includes('USER');
  }

  // Cek apakah token tidak valid (kosong atau expired)
  // Dipakai oleh authGuard untuk protect routes
  isTokenNotValid(): boolean {
    const token = this.token;
    return !token || this.jwtHelper.isTokenExpired(token); // true = expired atau tidak ada
  }

  // Hapus semua token dari localStorage (dipanggil saat logout)
  clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.ROLES_KEY);
  }
}
