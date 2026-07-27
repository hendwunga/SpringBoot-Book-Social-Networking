/* tslint:disable */
/* eslint-disable */
import { UserResponse } from './user-response';

export interface PageResponseUserResponse {
  content?: UserResponse[];
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
  first?: boolean;
  last?: boolean;
}
