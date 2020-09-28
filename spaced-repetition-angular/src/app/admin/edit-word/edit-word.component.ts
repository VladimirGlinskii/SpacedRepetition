import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {EditWordRequest} from '../../model/dto/request/word/edit-word-request';

@Component({
  selector: 'app-edit-word',
  templateUrl: './edit-word.component.html',
  styleUrls: ['./edit-word.component.css']
})
export class EditWordComponent implements OnInit {

  @Input() wordId: number;
  @Input() wordModel: EditWordRequest = {
    translation: '',
    word: ''
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

  editWord(): void {
    this.adminService.editWord(this.wordModel, this.wordId).subscribe(
      res => {
        this.wordModel.word = null;
        this.wordModel.translation = null;
        this.activeModal.close('Edited');
      },
      error => {
        this.activeModal.dismiss('Error');
        this.handleError(error);
      }
    );
  }

}
