import {Component, Input, OnInit} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {Dictionary} from '../../model/dictionary';
import {ApiUserService} from '../../service/api-user.service';

@Component({
  selector: 'app-select-dictionary-modal',
  templateUrl: './select-dictionary-modal.component.html',
  styleUrls: ['./select-dictionary-modal.component.css']
})
export class SelectDictionaryModalComponent implements OnInit {


  @Input() dictionary: Dictionary = {
    name: '',
    id: 0,
  };
  processing = false;

  constructor(private userService: ApiUserService,
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

  selectDictionary(): void {
    this.processing = true;
    this.userService.selectWholeDictionary(this.dictionary.id).subscribe(
      res => {
        this.activeModal.close('Selected');
        this.processing = false;
      },
      error => {
        this.activeModal.dismiss('Error');
        this.processing = false;
        this.handleError(error);
      }
    );
  }

}
