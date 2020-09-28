import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpRequest} from '@angular/common/http';
import {APP_CONFIG, AppConfig} from '../app-config';
import {Observable} from 'rxjs';
import {GetUsersResponse} from '../model/dto/response/user/get-users-response';
import {AddWordRequest} from '../model/dto/request/word/add-word-request';
import {AddDictionaryRequest} from '../model/dto/request/dictionary/add-dictionary-request';
import {Dictionary} from '../model/dictionary';
import {EditWordRequest} from '../model/dto/request/word/edit-word-request';
import {WordsDtoResponse} from '../model/dto/response/word/words-dto-response';
import {RenameDictionaryRequest} from '../model/dto/request/dictionary/rename-dictionary-request';

@Injectable({
  providedIn: 'root'
})
export class ApiAdminService {
  private BASIC_URL = this.config.apiEndpoint + 'admin/';
  private DICTIONARIES_URL = this.BASIC_URL + 'dictionaries/';


  constructor(private http: HttpClient,
              @Inject(APP_CONFIG) private config: AppConfig) {
  }

  getUsers(page: number, pageSize: number, filter: string): Observable<GetUsersResponse> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('pageSize', String(pageSize))
      .set('filter', filter);
    return this.http.get<GetUsersResponse>(this.BASIC_URL + 'users', {params});
  }

  getWords(page: number, pageSize: number, filter: string): Observable<WordsDtoResponse> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('pageSize', String(pageSize))
      .set('filter', filter);
    return this.http.get<WordsDtoResponse>(this.BASIC_URL + 'words', {params});
  }

  addWord(request: AddWordRequest, dictionary: Dictionary): Observable<any> {
    let params = new HttpParams();
    if (dictionary != null) {
      params = params.set('dictionaryId', String(dictionary.id));
    }
    return this.http.post(this.BASIC_URL + 'words', request, {params});
  }

  editWord(request: EditWordRequest, wordId: number): Observable<any> {
    return this.http.put(this.BASIC_URL + 'words/' + wordId, request);
  }

  deleteWord(wordId: number): Observable<any> {
    return this.http.delete(this.BASIC_URL + 'words/' + wordId);
  }

  deleteUser(userId: number): Observable<any> {
    return this.http.delete(this.BASIC_URL + 'users/' + userId);
  }

  deleteDictionary(dictionaryId: number): Observable<any> {
    return this.http.delete(this.DICTIONARIES_URL + dictionaryId);
  }

  addDictionary(request: AddDictionaryRequest): Observable<Dictionary> {
    return this.http.post<Dictionary>(this.DICTIONARIES_URL, request);
  }

  renameDictionary(request: RenameDictionaryRequest, dictionaryId: number): Observable<any> {
    return this.http.put(this.BASIC_URL + 'dictionaries/' + dictionaryId, request);
  }

  addWordToDictionary(dictionaryId: number, wordId: number): Observable<any> {
    return this.http.put(this.DICTIONARIES_URL + dictionaryId + '/words/' + wordId, {});
  }

  deleteWordFromDictionary(dictionaryId: number, wordId: number): Observable<any> {
    return this.http.delete(this.DICTIONARIES_URL + dictionaryId + '/words/' + wordId);
  }

  uploadWordsFromFile(file: File, dictionary: Dictionary): Observable<any> {
    const formData = new FormData();
    let params = new HttpParams();
    if (dictionary != null) {
      params = params.set('dictionaryId', String(dictionary.id));
    }
    formData.append('file', file);
    const req = new HttpRequest('POST', this.BASIC_URL + 'words/upload', formData, {params});
    return this.http.request(req);
  }
}
