import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Dictionary} from '../../../model/dictionary';
import {Word} from '../../../model/word';
import {ApiWordService} from '../../../service/api-word.service';
import {ApiAdminService} from '../../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../../../model/dto/response/errors-response';
import {ErrorResponse} from '../../../model/dto/response/error-response';
import {EditWordComponent} from '../../edit-word/edit-word.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeleteWordModalComponent} from '../../delete-word-modal/delete-word-modal.component';

@Component({
  selector: 'app-dictionary-words',
  templateUrl: './dictionary-words.component.html',
  styleUrls: ['./dictionary-words.component.css']
})
export class DictionaryWordsComponent implements OnInit {

  dictionary: Dictionary;
  page: number;
  pageSize: number;
  totalPages: number;
  searchTerm: string;
  words: Word[];
  showAllWords: boolean;
  @Output() dictionarySelected: EventEmitter<Dictionary> = new EventEmitter<Dictionary>();

  constructor(private wordService: ApiWordService,
              private adminService: ApiAdminService,
              private snackBar: MatSnackBar,
              private modalService: NgbModal) {
    this.pageSize = 10;
    this.showAllWords = false;
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

  selectDictionary(dictionary: Dictionary, page: number): void {
    this.wordService.getWordsForDictionary(dictionary.id, page, this.pageSize, this.showAllWords, this.searchTerm).subscribe(
      res => {
        this.page = page;
        this.words = res.words;
        this.dictionary = dictionary;
        this.totalPages = res.totalPages;
        this.dictionarySelected.emit(dictionary);
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
        this.selectDictionary(this.dictionary, 0);
      }
    }, 1000);
  }

  onShowAllChange(): void {
    if (this.showAllWords) {
      try {
        this.selectDictionary(this.dictionary, 0);
      } catch (e) {
        this.showAllWords = false;
      }
    } else {
      try {
        this.selectDictionary(this.dictionary, 0);
      } catch (e) {
        this.showAllWords = true;
      }
    }
  }

  addWordToDictionary(word: Word): void {
    this.adminService.addWordToDictionary(this.dictionary.id, word.id).subscribe(
      res => {
        word.selected = true;
      },
      error => {
        this.handleError(error);
      }
    );
  }

  deleteWordFromDictionary(word: Word): void {
    this.adminService.deleteWordFromDictionary(this.dictionary.id, word.id).subscribe(
      res => {
        this.selectDictionary(this.dictionary, this.page);
      },
      error => {
        this.handleError(error);
      }
    );
  }

  resetPage(): void {
    this.page = null;
    this.totalPages = null;
    this.dictionary = null;
    this.words = null;
  }

  toBegin(): void {
    this.wordService.getWordsForDictionary(this.dictionary.id, 0, this.pageSize, this.showAllWords, this.searchTerm).subscribe(
      res => {
        this.page = 0;
        this.totalPages = res.totalPages;
        this.words = res.words;
      },
      error => this.handleError(error)
    );
  }

  toEnd(): void {
    this.wordService.getWordsForDictionary(this.dictionary.id, this.totalPages - 1, this.pageSize,
      this.showAllWords, this.searchTerm).subscribe(
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
      this.wordService.getWordsForDictionary(this.dictionary.id, this.page + 1, this.pageSize,
        this.showAllWords, this.searchTerm).subscribe(
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
      this.wordService.getWordsForDictionary(this.dictionary.id, this.page - 1, this.pageSize,
        this.showAllWords, this.searchTerm).subscribe(
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
        this.selectDictionary(this.dictionary, this.page);
      }
    });
  }

  delete(word: Word) {
    const modalRef = this.modalService.open(DeleteWordModalComponent);
    modalRef.componentInstance.wordModel = word;
    modalRef.result.then(result => {
      if (result === 'Deleted') {
        this.selectDictionary(this.dictionary, this.page);
      }
    });
  }
}
