/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { authenticate } from '../fn/authentication/authenticate';
import { Authenticate$Params } from '../fn/authentication/authenticate';
import { AuthenticationResponse } from '../models/authentication-response';
import { confirm } from '../fn/authentication/confirm';
import { Confirm$Params } from '../fn/authentication/confirm';
import { refreshToken } from '../fn/authentication/refresh-token';
import { RefreshToken$Params } from '../fn/authentication/refresh-token';
import { register } from '../fn/authentication/register';
import { Register$Params } from '../fn/authentication/register';

@Injectable({ providedIn: 'root' })
export class AuthenticationService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `register()` */
  static readonly RegisterPath = '/auth/register';

  /**
   * Register a new user account.
   *
   * Creates a new user and sends an activation email with a 6-digit OTP code.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `register()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  register$Response(params: Register$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return register(this.http, this.rootUrl, params, context);
  }

  /**
   * Register a new user account.
   *
   * Creates a new user and sends an activation email with a 6-digit OTP code.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `register$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  register(params: Register$Params, context?: HttpContext): Observable<void> {
    return this.register$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `authenticate()` */
  static readonly AuthenticatePath = '/auth/authenticate';

  /**
   * Login and receive JWT tokens.
   *
   * Authenticates user credentials and returns access token, refresh token, and roles.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `authenticate()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authenticate$Response(params: Authenticate$Params, context?: HttpContext): Observable<StrictHttpResponse<AuthenticationResponse>> {
    return authenticate(this.http, this.rootUrl, params, context);
  }

  /**
   * Login and receive JWT tokens.
   *
   * Authenticates user credentials and returns access token, refresh token, and roles.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `authenticate$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authenticate(params: Authenticate$Params, context?: HttpContext): Observable<AuthenticationResponse> {
    return this.authenticate$Response(params, context).pipe(
      map((r: StrictHttpResponse<AuthenticationResponse>): AuthenticationResponse => r.body)
    );
  }

  /** Path part for operation `confirm()` */
  static readonly ConfirmPath = '/auth/activate-account';

  /**
   * Activate account with OTP code.
   *
   * Activates a user account using the 6-digit code sent via email.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `confirm()` instead.
   *
   * This method doesn't expect any request body.
   */
  confirm$Response(params: Confirm$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return confirm(this.http, this.rootUrl, params, context);
  }

  /**
   * Activate account with OTP code.
   *
   * Activates a user account using the 6-digit code sent via email.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `confirm$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  confirm(params: Confirm$Params, context?: HttpContext): Observable<void> {
    return this.confirm$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `refreshToken()` */
  static readonly RefreshTokenPath = '/auth/refresh-token';

  /**
   * Refresh access token.
   *
   * Generates new access and refresh tokens using a valid refresh token.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `refreshToken()` instead.
   *
   * This method doesn't expect any request body.
   */
  refreshToken$Response(params: RefreshToken$Params, context?: HttpContext): Observable<StrictHttpResponse<AuthenticationResponse>> {
    return refreshToken(this.http, this.rootUrl, params, context);
  }

  /**
   * Refresh access token.
   *
   * Generates new access and refresh tokens using a valid refresh token.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `refreshToken$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  refreshToken(params: RefreshToken$Params, context?: HttpContext): Observable<AuthenticationResponse> {
    return this.refreshToken$Response(params, context).pipe(
      map((r: StrictHttpResponse<AuthenticationResponse>): AuthenticationResponse => r.body)
    );
  }

}
