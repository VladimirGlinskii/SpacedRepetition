import {Component, Inject, OnInit} from '@angular/core';
import {APP_CONFIG, AppConfig} from '../app-config';
import {RegisterDtoRequest} from '../model/dto/request/user/register-dto-request';
import {ApiUserService} from '../service/api-user.service';
import {Router} from '@angular/router';
import {ErrorCode} from '../exception/error-code.enum';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../model/dto/response/errors-response';
import {ErrorResponse} from '../model/dto/response/error-response';

@Component({
  selector: 'app-registration-form',
  templateUrl: './registration-form.component.html',
  styleUrls: ['./registration-form.component.css']
})

export class RegistrationFormComponent implements OnInit {
  model: RegisterDtoRequest = {
    username: '',
    email: '',
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

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, null, {
      duration: 2000, panelClass: ['standard-snackbar']
    });
  }

  register(): void {
    this.userService.register(this.model).subscribe(
      res => {
        localStorage.setItem('id', String(res.id));
        localStorage.setItem('username', res.username);
        this.router.navigateByUrl('/login');
      },
      err => this.handleError(err)
    );
  }

}
