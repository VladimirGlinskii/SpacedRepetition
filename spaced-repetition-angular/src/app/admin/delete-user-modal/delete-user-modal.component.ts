import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Word} from '../../model/word';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {User} from '../../model/user';

@Component({
  selector: 'app-delete-user-modal',
  templateUrl: './delete-user-modal.component.html',
  styleUrls: ['./delete-user-modal.component.css']
})
export class DeleteUserModalComponent implements OnInit {

  @Input() userModel: User = {
    id: 0,
    username: '',
    email: '',
    roles: null
  };

  constructor(private adminService: ApiAdminService,
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

  deleteUser(): void {
    this.adminService.deleteUser(this.userModel.id).subscribe(
      res => {
        this.activeModal.close('Deleted');
      },
      error => {
        this.activeModal.dismiss('Error');
        this.handleError(error);
      }
    );
  }
}
