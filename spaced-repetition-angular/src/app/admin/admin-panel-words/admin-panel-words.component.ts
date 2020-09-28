import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Word} from '../../model/word';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {EditWordComponent} from '../edit-word/edit-word.component';
import {DeleteWordModalComponent} from '../delete-word-modal/delete-word-modal.component';

@Component({
  selector: 'app-admin-panel-words',
  templateUrl: './admin-panel-words.component.html',
  styleUrls: ['./admin-panel-words.component.css']
})
export class AdminPanelWordsComponent implements OnInit {

  page: number;
  pageSize: number;
  totalPages: number;
  searchTerm: string;
  words: Word[];
  @Output() updated: EventEmitter<any> = new EventEmitter<any>();

  constructor(private adminService: ApiAdminService,
              private snackBar: MatSnackBar,
              private modalService: NgbModal) {
    this.pageSize = 10;
    this.searchTerm = '';
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

  getWords(page: number): void {
    this.adminService.getWords(page, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = page;
        this.words = res.words;
        this.totalPages = res.totalPages;
        this.updated.emit();
      },
      error => {
        this.handleError(error);
        throw error;
      }
    );
  }

  filter(): void {
    const tmpSearchTerm = this.searchTerm;
    setTimeout(() => {
      if (tmpSearchTerm === this.searchTerm) {
        this.getWords(0);
      }
    }, 1000);
  }

  resetPage(): void {
    this.page = null;
    this.totalPages = null;
    this.words = null;
  }

  toBegin(): void {
    this.adminService.getWords(0, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = 0;
        this.totalPages = res.totalPages;
        this.words = res.words;
      },
      error => this.handleError(error)
    );
  }

  toEnd(): void {
    this.adminService.getWords(this.totalPages - 1, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = res.totalPages - 1;
        this.totalPages = res.totalPages;
        this.words = res.words;
      },
      error => this.handleError(error)
    );
  }

  next(): void {
    if (this.page < this.totalPages - 1) {
      this.adminService.getWords(this.page + 1, this.pageSize, this.searchTerm).subscribe(
        res => {
          this.page++;
          this.totalPages = res.totalPages;
          this.words = res.words;
        },
        error => this.handleError(error)
      );
    }
  }

  previous(): void {
    if (this.page > 0) {
      this.adminService.getWords(this.page - 1, this.pageSize, this.searchTerm).subscribe(
        res => {
          this.page--;
          this.totalPages = res.totalPages;
          this.words = res.words;
        },
        error => this.handleError(error)
      );
    }
  }

  edit(word: Word) {
    const modalRef = this.modalService.open(EditWordComponent);
    modalRef.componentInstance.wordModel.word = word.name;
    modalRef.componentInstance.wordModel.translation = word.translation.toString();
    modalRef.componentInstance.wordId = word.id;
    modalRef.result.then(result => {
      if (result === 'Edited') {
        this.getWords(this.page);
      }
    });
  }

  delete(word: Word) {
    const modalRef = this.modalService.open(DeleteWordModalComponent);
    modalRef.componentInstance.wordModel = word;
    modalRef.result.then(result => {
      if (result === 'Deleted') {
        this.getWords(this.page);
      }
    });
  }

}
