import {Component, Inject, Input, OnInit} from '@angular/core';
import {APP_CONFIG, AppConfig} from '../../app-config';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ApiUserService} from '../../service/api-user.service';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {ChangePasswordRequest} from '../../model/dto/request/user/change-password-request';

@Component({
  selector: 'app-change-password-modal',
  templateUrl: './change-password-modal.component.html',
  styleUrls: ['./change-password-modal.component.css']
})
export class ChangePasswordModalComponent implements OnInit {

  @Input() model: ChangePasswordRequest = {
    id: 0,
    oldPassword: '',
    newPassword: ''
  };

  constructor(@Inject(APP_CONFIG) public config: AppConfig,
              private userService: ApiUserService,
              private snackBar: MatSnackBar,
              public activeModal: NgbActiveModal) {

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

  changePassword(): void {
    this.userService.changePassword(this.model).subscribe(
      res => {
        this.activeModal.close('Changed');
      },
      error => {
        this.activeModal.dismiss('Error');
        this.handleError(error);
      }
    );
  }

}
