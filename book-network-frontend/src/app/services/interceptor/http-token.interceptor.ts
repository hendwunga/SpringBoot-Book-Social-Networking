import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {TokenService} from '../token/token.service';
import {inject} from '@angular/core';

// HTTP Interceptor — otomatis tambahkan header Authorization: Bearer <token> ke SEMUA request HTTP
// Terhubung ke backend: JwtFilter.java — menerima token dari header ini
export class HttpTokenInterceptor implements HttpInterceptor {
  tokenService = inject(TokenService); // Inject TokenService

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenService.token; // Ambil access token dari localStorage

    // Jika token ada, clone request dan tambahkan header Authorization
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}` // Format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        }
      });
    }

    // Lanjutkan request (dengan atau tanpa header)
    return next.handle(req);
  }
}
