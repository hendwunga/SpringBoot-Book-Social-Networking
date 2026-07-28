/* tslint:disable */
/* eslint-disable */
import { UserResponse } from '../models/user-response';
export interface PageResponseUserResponse {
  content?: Array<UserResponse>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
