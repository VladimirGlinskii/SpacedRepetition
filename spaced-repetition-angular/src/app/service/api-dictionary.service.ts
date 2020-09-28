import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {APP_CONFIG, AppConfig} from '../app-config';
import {Observable} from 'rxjs';
import {DictionariesDtoResponse} from '../model/dto/response/dictionary/dictionaries-dto-response';

@Injectable({
  providedIn: 'root'
})
export class ApiDictionaryService {
  private BASIC_URL = this.config.apiEndpoint + 'dictionaries';

  constructor(private http: HttpClient,
              @Inject(APP_CONFIG) private config: AppConfig) {
  }

  getDictionaries(page: number, pageSize: number, filter: string): Observable<DictionariesDtoResponse> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('pageSize', String(pageSize))
      .set('filter', filter);
    return this.http.get<DictionariesDtoResponse>(this.BASIC_URL, {params});
  }
}
