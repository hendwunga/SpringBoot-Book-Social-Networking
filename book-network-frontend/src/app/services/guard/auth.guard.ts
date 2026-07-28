import {CanActivateFn, Router} from '@angular/router';
import {TokenService} from '../token/token.service';
import {inject} from '@angular/core';

// Route Guard — melindungi route yang butuh login
// Terhubung ke backend: SecurityConfig.java — enforce authentication
export const authGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  // Cek apakah token tidak valid (kosong atau expired)
  if (tokenService.isTokenNotValid()) {
    router.navigate(['login']); // Redirect ke halaman login
    return false;               // Blokir akses ke route
  }

  return true; // Token valid → izinkan akses
};
