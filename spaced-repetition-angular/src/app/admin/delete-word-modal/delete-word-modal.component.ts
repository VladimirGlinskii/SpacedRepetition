import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {Word} from '../../model/word';

@Component({
  selector: 'app-delete-word-modal',
  templateUrl: './delete-word-modal.component.html',
  styleUrls: ['./delete-word-modal.component.css']
})
export class DeleteWordModalComponent implements OnInit {

  @Input() wordModel: Word = {
    translation: null,
    name: '',
    id: 0,
    selected: true
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

  deleteWord(): void {
    this.adminService.deleteWord(this.wordModel.id).subscribe(
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
