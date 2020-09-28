import {Component, Input, OnInit} from '@angular/core';
import {EditWordRequest} from '../../model/dto/request/word/edit-word-request';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {RenameDictionaryRequest} from '../../model/dto/request/dictionary/rename-dictionary-request';

@Component({
  selector: 'app-rename-dictionary-modal',
  templateUrl: './rename-dictionary-modal.component.html',
  styleUrls: ['./rename-dictionary-modal.component.css']
})
export class RenameDictionaryModalComponent implements OnInit {

  @Input() dictionaryId: number;
  @Input() model: RenameDictionaryRequest = {
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

  renameDictionary(): void {
    this.adminService.renameDictionary(this.model, this.dictionaryId).subscribe(
      res => {
        this.model = null;
        this.activeModal.close('Renamed');
      },
      error => {
        this.activeModal.dismiss('Error');
        this.handleError(error);
      }
    );
  }

}
