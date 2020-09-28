import {Component, Input, OnInit} from '@angular/core';
import {Word} from '../../model/word';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {Dictionary} from '../../model/dictionary';

@Component({
  selector: 'app-delete-dictionary-modal',
  templateUrl: './delete-dictionary-modal.component.html',
  styleUrls: ['./delete-dictionary-modal.component.css']
})
export class DeleteDictionaryModalComponent implements OnInit {

  @Input() dictionaryModel: Dictionary = {
    id: 0,
    name: ''
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

  deleteDictionary(): void {
    this.adminService.deleteDictionary(this.dictionaryModel.id).subscribe(
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
