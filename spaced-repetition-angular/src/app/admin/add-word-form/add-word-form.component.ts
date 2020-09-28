import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AddWordRequest} from '../../model/dto/request/word/add-word-request';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Dictionary} from '../../model/dictionary';

@Component({
  selector: 'app-add-word-form',
  templateUrl: './add-word-form.component.html',
  styleUrls: ['./add-word-form.component.css']
})
export class AddWordFormComponent implements OnInit {
  closeResult = '';
  wordModel: AddWordRequest = {
    translation: '',
    word: ''
  };
  uploadFromFile = false;
  file: File = null;
  @Input() dictionary: Dictionary;
  @Output() wordAdded: EventEmitter<any> = new EventEmitter<any>();

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

  addWord(): void {
    this.adminService.addWord(this.wordModel, this.dictionary).subscribe(
      res => {
        this.wordAdded.emit();
        this.wordModel.word = null;
        this.wordModel.translation = null;
        this.file = null;
        this.uploadFromFile = false;
        this.modalService.dismissAll();
        this.snackBar.open('Word successfully added', null, {
          duration: 3000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar', 'green']
        });
      },
      error => {
        this.modalService.dismissAll(this.closeResult);
        this.handleError(error);
      }
    );
  }

  fileChange(event): void {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.file = fileList[0];
    }
  }

  uploadFile(): void {
    if (this.file != null) {
      this.adminService.uploadWordsFromFile(this.file, this.dictionary).subscribe(
        res => {
        },
        error => {
          this.modalService.dismissAll(this.closeResult);
          this.handleError(error);
        },
        () => {
          this.wordAdded.emit();
          this.file = null;
          this.uploadFromFile = false;
          this.wordModel.word = null;
          this.wordModel.translation = null;
          this.modalService.dismissAll();
          this.snackBar.open('File successfully uploaded', null, {
            duration: 3000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar', 'green']
          });
        }
      );
    }
  }

}
