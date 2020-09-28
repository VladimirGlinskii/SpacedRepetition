import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Word} from '../../model/word';
import {ApiWordService} from '../../service/api-word.service';
import {Dictionary} from '../../model/dictionary';
import {ApiUserService} from '../../service/api-user.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../../model/dto/response/errors-response';
import {ErrorResponse} from '../../model/dto/response/error-response';

@Component({
  selector: 'app-words',
  templateUrl: './words.component.html',
  styleUrls: ['./words.component.css']
})
export class WordsComponent implements OnInit {
  dictionary: Dictionary;
  page: number;
  pageSize: number;
  totalPages: number;
  searchTerm: string;
  words: Word[];
  @Output() dictionarySelected: EventEmitter<Dictionary> = new EventEmitter<Dictionary>();

  constructor(private wordService: ApiWordService,
              private userService: ApiUserService,
              private snackBar: MatSnackBar) {
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

  selectDictionary(dictionary: Dictionary, page: number): void {
    this.wordService.getDictionaryWordsForUser(dictionary.id, page, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.dictionary = dictionary;
        this.words = res.words;
        this.page = page;
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

  selectWord(word: Word): void {
    this.userService.selectWord(word.id).subscribe(
      res => {
        word.selected = true;
      },
      error => {
        this.handleError(error);
      }
    );
  }

  deselectWord(word: Word): void {
    this.userService.deleteWord(word.id).subscribe(
      res => {
        word.selected = false;
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
    this.wordService.getDictionaryWordsForUser(this.dictionary.id, 0, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = 0;
        this.totalPages = res.totalPages;
        this.words = res.words;
      },
      error => this.handleError(error)
    );
  }

  toEnd(): void {
    this.wordService.getDictionaryWordsForUser(this.dictionary.id, this.totalPages - 1, this.pageSize, this.searchTerm).subscribe(
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
      this.wordService.getDictionaryWordsForUser(this.dictionary.id, this.page + 1, this.pageSize, this.searchTerm).subscribe(
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
      this.wordService.getDictionaryWordsForUser(this.dictionary.id, this.page - 1, this.pageSize, this.searchTerm).subscribe(
        res => {
          this.page--;
          this.totalPages = res.totalPages;
          this.words = res.words;
        },
        error => this.handleError(error)
      );
    }
  }

}
