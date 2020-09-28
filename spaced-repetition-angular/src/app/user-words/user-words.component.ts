import {Component, OnInit, ViewChild} from '@angular/core';
import {Dictionary} from '../model/dictionary';
import {ApiDictionaryService} from '../service/api-dictionary.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../model/dto/response/errors-response';
import {ErrorResponse} from '../model/dto/response/error-response';
import {WordsComponent} from './words/words.component';
import {NavigationEnd, Router} from '@angular/router';
import {ApiUserService} from '../service/api-user.service';
import {DeleteWordModalComponent} from '../admin/delete-word-modal/delete-word-modal.component';
import {UnselectDictionaryModalComponent} from './unselect-dictionary-modal/unselect-dictionary-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {SelectDictionaryModalComponent} from './select-dictionary-modal/select-dictionary-modal.component';

@Component({
  selector: 'app-user-words',
  templateUrl: './user-words.component.html',
  styleUrls: ['./user-words.component.css']
})
export class UserWordsComponent implements OnInit {

  allWords: Dictionary = {
    id: 0,
    name: 'All Words'
  };
  dictionaries: Dictionary[];
  page: number;
  pageSize: number;
  totalPages: number;
  searchTerm: string;
  selectedDictionary: Dictionary;
  @ViewChild(WordsComponent) wordsComponent: WordsComponent;

  constructor(private dictionaryService: ApiDictionaryService,
              private userService: ApiUserService,
              private snackBar: MatSnackBar,
              private router: Router,
              private modalService: NgbModal) {
    this.pageSize = 10;
    this.selectedDictionary = null;
    this.searchTerm = '';
    router.events.subscribe((event) => {

      if (event instanceof NavigationEnd) {
        if (event.url === '/words') {
          this.getDictionaries(0);
        } else {
          this.resetPage();
        }
      }
    });

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
    if (this.selectedDictionary !== dictionary) {
      try {
        this.wordsComponent.selectDictionary(dictionary, 0);
      } catch (e) {
      }
    }
  }

  getDictionaries(page: number): void {
    this.dictionaryService.getDictionaries(0, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.dictionaries = res.dictionaries;
        this.page = 0;
        this.totalPages = res.totalPages;
      },
      error => {
        this.handleError(error);
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
    this.wordsComponent.resetPage();
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


  unselectWholeDictionary(d: Dictionary) {
    const modalRef = this.modalService.open(UnselectDictionaryModalComponent);
    modalRef.componentInstance.dictionary = d;
    modalRef.result.then(result => {
      if (result === 'Unselected' && this.selectedDictionary === d) {
        this.wordsComponent.selectDictionary(d, this.wordsComponent.page);
      }
    });
  }


  selectWholeDictionary(d: Dictionary) {
    const modalRef = this.modalService.open(SelectDictionaryModalComponent);
    modalRef.componentInstance.dictionary = d;
    modalRef.result.then(result => {
      if (result === 'Selected' && this.selectedDictionary === d) {
        this.wordsComponent.selectDictionary(d, this.wordsComponent.page);
      }
    });
  }
}
