import {RoleName} from './enum/role-name.enum';

export interface User {
  id: number;
  username: string;
  email: string;
  roles: RoleName[];
}
