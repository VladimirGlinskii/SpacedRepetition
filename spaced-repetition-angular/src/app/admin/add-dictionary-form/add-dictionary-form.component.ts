import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ApiAdminService} from '../../service/api-admin.service';
import {AddDictionaryRequest} from '../../model/dto/request/dictionary/add-dictionary-request';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-add-dictionary-form',
  templateUrl: './add-dictionary-form.component.html',
  styleUrls: ['./add-dictionary-form.component.css']
})
export class AddDictionaryFormComponent implements OnInit {
  closeResult = '';
  dictionaryModel: AddDictionaryRequest = {
    name: ''
  };
  @Output() dictionaryAdded: EventEmitter<any> = new EventEmitter<any>();

  constructor(private adminService: ApiAdminService,
              private snackBar: MatSnackBar,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
  }

  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
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

  addDictionary(): void {
    this.adminService.addDictionary(this.dictionaryModel).subscribe(
      res => {
        this.dictionaryAdded.emit();
        this.dictionaryModel.name = null;
        this.modalService.dismissAll(this.closeResult);
        this.snackBar.open('Dictionary successfully added', null, {
          duration: 3000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar', 'green']
        });
      },
      error => {
        this.modalService.dismissAll(this.closeResult);
        this.handleError(error);
      }
    );
  }

}
