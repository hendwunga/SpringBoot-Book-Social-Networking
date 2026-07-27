/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { findAllUsers } from '../fn/user/find-all-users';
import { FindAllUsers$Params } from '../fn/user/find-all-users';
import { findUserById } from '../fn/user/find-user-by-id';
import { FindUserById$Params } from '../fn/user/find-user-by-id';
import { getOwnProfile } from '../fn/user/get-own-profile';
import { GetOwnProfile$Params } from '../fn/user/get-own-profile';
import { PageResponseUserResponse } from '../models/page-response-user-response';
import { toggleAccountEnabled } from '../fn/user/toggle-account-enabled';
import { ToggleAccountEnabled$Params } from '../fn/user/toggle-account-enabled';
import { toggleAccountLock } from '../fn/user/toggle-account-lock';
import { ToggleAccountLock$Params } from '../fn/user/toggle-account-lock';
import { UserResponse } from '../models/user-response';

@Injectable({ providedIn: 'root' })
export class UserService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `findAllUsers()` */
  static readonly FindAllUsersPath = '/users';

  /**
   * Get all users - Admin only.
   *
   * Returns paginated list of all registered users. Requires ADMIN role.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `findAllUsers()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllUsers$Response(params?: FindAllUsers$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseUserResponse>> {
    return findAllUsers(this.http, this.rootUrl, params, context);
  }

  /**
   * Get all users - Admin only.
   *
   * Returns paginated list of all registered users. Requires ADMIN role.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `findAllUsers$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllUsers(params?: FindAllUsers$Params, context?: HttpContext): Observable<PageResponseUserResponse> {
    return this.findAllUsers$Response(params, context).pipe(
      map((r: StrictHttpResponse<PageResponseUserResponse>): PageResponseUserResponse => r.body)
    );
  }

  /** Path part for operation `findUserById()` */
  static readonly FindUserByIdPath = '/users/{user-id}';

  /**
   * Get user by ID - Admin only.
   *
   * Returns user details. Requires ADMIN role.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `findUserById()` instead.
   *
   * This method doesn't expect any request body.
   */
  findUserById$Response(params: FindUserById$Params, context?: HttpContext): Observable<StrictHttpResponse<UserResponse>> {
    return findUserById(this.http, this.rootUrl, params, context);
  }

  /**
   * Get user by ID - Admin only.
   *
   * Returns user details. Requires ADMIN role.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `findUserById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  findUserById(params: FindUserById$Params, context?: HttpContext): Observable<UserResponse> {
    return this.findUserById$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserResponse>): UserResponse => r.body)
    );
  }

  /** Path part for operation `toggleAccountLock()` */
  static readonly ToggleAccountLockPath = '/users/{user-id}/lock';

  /**
   * Toggle user lock status - Admin only.
   *
   * Locks or unlocks a user account. Requires ADMIN role.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `toggleAccountLock()` instead.
   *
   * This method doesn't expect any request body.
   */
  toggleAccountLock$Response(params: ToggleAccountLock$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return toggleAccountLock(this.http, this.rootUrl, params, context);
  }

  /**
   * Toggle user lock status - Admin only.
   *
   * Locks or unlocks a user account. Requires ADMIN role.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `toggleAccountLock$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  toggleAccountLock(params: ToggleAccountLock$Params, context?: HttpContext): Observable<void> {
    return this.toggleAccountLock$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `toggleAccountEnabled()` */
  static readonly ToggleAccountEnabledPath = '/users/{user-id}/enable';

  /**
   * Toggle user enabled status - Admin only.
   *
   * Enables or disables a user account. Requires ADMIN role.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `toggleAccountEnabled()` instead.
   *
   * This method doesn't expect any request body.
   */
  toggleAccountEnabled$Response(params: ToggleAccountEnabled$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return toggleAccountEnabled(this.http, this.rootUrl, params, context);
  }

  /**
   * Toggle user enabled status - Admin only.
   *
   * Enables or disables a user account. Requires ADMIN role.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `toggleAccountEnabled$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  toggleAccountEnabled(params: ToggleAccountEnabled$Params, context?: HttpContext): Observable<void> {
    return this.toggleAccountEnabled$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getOwnProfile()` */
  static readonly GetOwnProfilePath = '/users/profile';

  /**
   * Get own profile - Any authenticated user.
   *
   * Returns the current user's profile information.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getOwnProfile()` instead.
   *
   * This method doesn't expect any request body.
   */
  getOwnProfile$Response(params?: GetOwnProfile$Params, context?: HttpContext): Observable<StrictHttpResponse<UserResponse>> {
    return getOwnProfile(this.http, this.rootUrl, params, context);
  }

  /**
   * Get own profile - Any authenticated user.
   *
   * Returns the current user's profile information.
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getOwnProfile$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getOwnProfile(params?: GetOwnProfile$Params, context?: HttpContext): Observable<UserResponse> {
    return this.getOwnProfile$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserResponse>): UserResponse => r.body)
    );
  }

}
