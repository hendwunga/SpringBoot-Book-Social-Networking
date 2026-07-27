/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { PageResponseUserResponse } from '../models/page-response-user-response';
import { UserResponse } from '../models/user-response';

@Injectable({ providedIn: 'root' })
export class UserService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  static readonly FindAllUsersPath = '/users';

  findAllUsers(params?: { page?: number; size?: number }): Observable<PageResponseUserResponse> {
    return this.http.get<PageResponseUserResponse>(
      `${this.rootUrl}/users?page=${params?.page || 0}&size=${params?.size || 10}`
    );
  }

  findUserById(userId: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.rootUrl}/users/${userId}`);
  }

  toggleAccountLock(userId: number): Observable<void> {
    return this.http.patch<void>(`${this.rootUrl}/users/${userId}/lock`, {});
  }

  toggleAccountEnabled(userId: number): Observable<void> {
    return this.http.patch<void>(`${this.rootUrl}/users/${userId}/enable`, {});
  }

  getOwnProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.rootUrl}/users/profile`);
  }
}
