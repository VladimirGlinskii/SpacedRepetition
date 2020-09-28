import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {APP_CONFIG, AppConfig} from '../app-config';
import {Observable} from 'rxjs';
import {WordsDtoResponse} from '../model/dto/response/word/words-dto-response';
import {PronunciationLinkResponse} from '../model/dto/response/word/pronunciation-link-response';

@Injectable({
  providedIn: 'root'
})
export class ApiWordService {
  private BASIC_URL = this.config.apiEndpoint + 'words/';
  private GET_WORDS_URL = this.config.apiEndpoint + 'dictionaries/';

  constructor(private http: HttpClient,
              @Inject(APP_CONFIG) private config: AppConfig) {
  }

  getDictionaryWordsForUser(dictionaryId: number, page: number, pageSize: number, filter: string): Observable<WordsDtoResponse> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('pageSize', String(pageSize))
      .set('filter', filter);
    return this.http.get<WordsDtoResponse>(this.GET_WORDS_URL + String(dictionaryId), {params});
  }

  getWordsForDictionary(id: number, page: number, pageSize: number, showAll: boolean, filter: string): Observable<WordsDtoResponse> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('pageSize', String(pageSize))
      .set('showAll', String(showAll))
      .set('filter', filter);
    return this.http.get<WordsDtoResponse>(this.BASIC_URL + 'dictionary/' + String(id), {params});
  }

  getWordPronunciationLink(word: string): Observable<PronunciationLinkResponse> {
    return this.http.get<PronunciationLinkResponse>(this.BASIC_URL + word + '/pronunciation');
  }
}
