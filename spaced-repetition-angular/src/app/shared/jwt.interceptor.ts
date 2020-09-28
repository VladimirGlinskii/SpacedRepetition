import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {EMPTY, Observable} from 'rxjs';
import {ApiUserService} from '../service/api-user.service';
import {Router} from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private userService: ApiUserService,
              private router: Router) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    if (this.userService.isLogged()) {
      if (!request.url.includes('upload')) {
        request = request.clone({
          setHeaders: {
            Authorization: token,
            'Content-Type': 'application/json'
          }
        });
      } else {
        request = request.clone({
          setHeaders: {
            Authorization: token
          }
        });
      }
    } else {
      if (this.router.url !== '/login' && this.router.url !== '/registration') {
        this.router.navigateByUrl('/login');
        return EMPTY;
      }
    }
    /*request = request.clone({
      setHeaders: {
        'Content-Type': 'application/json'
      }
    });*/
    return next.handle(request);
  }
}
