import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {Dictionary} from '../../model/dictionary';
import {ApiDictionaryService} from '../../service/api-dictionary.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';
import {DictionaryWordsComponent} from './dictionary-words/dictionary-words.component';
import {User} from '../../model/user';
import {DeleteUserModalComponent} from '../delete-user-modal/delete-user-modal.component';
import {DeleteDictionaryModalComponent} from '../delete-dictionary-modal/delete-dictionary-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Word} from '../../model/word';
import {EditWordComponent} from '../edit-word/edit-word.component';
import {RenameDictionaryModalComponent} from '../rename-dictionary-modal/rename-dictionary-modal.component';

@Component({
  selector: 'app-admin-panel-dictionaries',
  templateUrl: './admin-panel-dictionaries.component.html',
  styleUrls: ['./admin-panel-dictionaries.component.css']
})
export class AdminPanelDictionariesComponent implements OnInit {

  dictionaries: Dictionary[];
  page: number;
  pageSize: number;
  totalPages: number;
  searchTerm: string;
  selectedDictionary: Dictionary;
  @Output() updated: EventEmitter<any> = new EventEmitter<any>();
  @ViewChild(DictionaryWordsComponent) dictionaryWords: DictionaryWordsComponent;

  constructor(private dictionaryService: ApiDictionaryService,
              private snackBar: MatSnackBar,
              private modalService: NgbModal) {
    this.pageSize = 10;
    this.selectedDictionary = null;
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

  selectDictionary(dictionary: Dictionary): void {
    try {
      this.dictionaryWords.selectDictionary(dictionary, 0);
      this.selectedDictionary = dictionary;
    } catch (e) {
    }
  }

  getDictionaries(page: number): void {
    this.dictionaryService.getDictionaries(page, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.dictionaries = res.dictionaries;
        this.page = 0;
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
          this.getDictionaries(0);
        }
      }, 1000);
  }

  resetPage(): void {
    this.page = null;
    this.totalPages = null;
    this.dictionaries = null;
    this.dictionaryWords.resetPage();
  }

  toBegin(): void {
    this.dictionaryService.getDictionaries(0, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = 0;
        this.totalPages = res.totalPages;
        this.dictionaries = res.dictionaries;
      },
      error => this.handleError(error)
    );
  }

  toEnd(): void {
    this.dictionaryService.getDictionaries(this.totalPages - 1, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = res.totalPages - 1;
        this.totalPages = res.totalPages;
        this.dictionaries = res.dictionaries;
      },
      error => this.handleError(error)
    );
  }

  next(): void {
    if (this.page < this.totalPages - 1) {
      this.dictionaryService.getDictionaries(this.page + 1, this.pageSize, this.searchTerm).subscribe(
        res => {
          this.page++;
          this.totalPages = res.totalPages;
          this.dictionaries = res.dictionaries;
        },
        error => this.handleError(error)
      );
    }
  }

  previous(): void {
    if (this.page > 0) {
      this.dictionaryService.getDictionaries(this.page - 1, this.pageSize, this.searchTerm).subscribe(
        res => {
          this.page--;
          this.totalPages = res.totalPages;
          this.dictionaries = res.dictionaries;
        },
        error => this.handleError(error)
      );
    }
  }

  remove(dictionary: Dictionary) {
    const modalRef = this.modalService.open(DeleteDictionaryModalComponent);
    modalRef.componentInstance.dictionaryModel = dictionary;
    modalRef.result.then(result => {
      this.dictionaryWords.resetPage();
      if (result === 'Deleted') {
        this.getDictionaries(this.page);
      }
    });
  }

  rename(dictionary: Dictionary) {
    const modalRef = this.modalService.open(RenameDictionaryModalComponent);
    modalRef.componentInstance.model.name = dictionary.name;
    modalRef.componentInstance.dictionaryId = dictionary.id;
    modalRef.result.then(result => {
      if (result === 'Renamed') {
        this.getDictionaries(this.page);
      }
    });
  }

}
