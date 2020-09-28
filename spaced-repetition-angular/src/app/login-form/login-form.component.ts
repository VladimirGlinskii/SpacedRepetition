import {Component, Inject, OnInit} from '@angular/core';
import {APP_CONFIG, AppConfig} from '../app-config';
import {LoginDtoRequest} from '../model/dto/request/user/login-dto-request';
import {ApiUserService} from '../service/api-user.service';
import {Router} from '@angular/router';
import {ErrorCode} from '../exception/error-code.enum';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../model/dto/response/errors-response';
import {ErrorResponse} from '../model/dto/response/error-response';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {
  model: LoginDtoRequest = {
    username: '',
    password: ''
  };

  constructor(private userService: ApiUserService,
              @Inject(APP_CONFIG) public config: AppConfig,
              private router: Router,
              private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
  }

  handleError(errorsResponse: ErrorsResponse): void {
    if (errorsResponse.errors != null) {
      const errors: ErrorResponse[] = errorsResponse.errors;
      for (let i = 0; i < errorsResponse.errors.length; i++) {
        this.snackBar.open(errors[i].message, null, {
          duration: (i + 1) * 3000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar']
        });
      }
    }
  }

  login(): void {
    this.userService.login(this.model).subscribe(
      res => {
        localStorage.setItem('id', String(res.id));
        localStorage.setItem('token', res.token);
        localStorage.setItem('username', this.model.username);
        localStorage.setItem('roles', JSON.stringify(res.roles));
        localStorage.setItem('email', res.email);
        this.router.navigateByUrl('/home');
      },
      err => this.handleError(err)
    );
  }

}
