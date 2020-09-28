import {RoleName} from '../../../enum/role-name.enum';

export interface LoginDtoResponse {
  id: number;
  email: string;
  token: string;
  roles: RoleName[];
}
