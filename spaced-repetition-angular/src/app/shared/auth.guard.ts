import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {ApiUserService} from '../service/api-user.service';
import {RoleName} from '../model/enum/role-name.enum';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router,
              public userService: ApiUserService) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot) {
    if (this.userService.isLogged()) {
      const roles: RoleName[] = JSON.parse(localStorage.getItem('roles'));
      const requiredRoles: RoleName[] = route.data.roles;
      if (requiredRoles && !requiredRoles.every(r => roles.indexOf(r) !== -1)) {
        this.router.navigateByUrl('/home');
        return false;
      }
      return true;
    }

    this.router.navigateByUrl('/login');
    return false;
  }

}
