import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {APP_CONFIG, AppConfig} from '../app-config';
import {Observable} from 'rxjs';
import {RegisterDtoRequest} from '../model/dto/request/user/register-dto-request';
import {LoginDtoRequest} from '../model/dto/request/user/login-dto-request';
import {RegisterDtoResponse} from '../model/dto/response/user/register-dto-response';
import {LoginDtoResponse} from '../model/dto/response/user/login-dto-response';
import {Router} from '@angular/router';
import {JwtHelperService} from '@auth0/angular-jwt';
import {RoleName} from '../model/enum/role-name.enum';
import {WordsDtoResponse} from '../model/dto/response/word/words-dto-response';
import {WeeklyStatisticsResponse} from '../model/dto/response/statistics/weekly-statistics-response';
import {ChangePasswordRequest} from '../model/dto/request/user/change-password-request';

@Injectable({
  providedIn: 'root'
})
export class ApiUserService {
  private REGISTRATION_URL = this.config.apiEndpoint + 'registration';
  private LOGIN_URL = this.config.apiEndpoint + 'login';
  private WORDS_URL = this.config.apiEndpoint + 'users/words/';
  private DICTIONARIES_URL = this.config.apiEndpoint + 'users/dictionaries/';
  private USERS_URL = this.config.apiEndpoint + 'users/';
  private helper = new JwtHelperService();

  constructor(private http: HttpClient,
              @Inject(APP_CONFIG) private config: AppConfig,
              private router: Router) {
  }

  register(model: RegisterDtoRequest): Observable<RegisterDtoResponse> {
    return this.http.post<RegisterDtoResponse>(this.REGISTRATION_URL, model);
  }

  login(model: LoginDtoRequest): Observable<LoginDtoResponse> {
    return this.http.put<LoginDtoResponse>(this.LOGIN_URL, model);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigateByUrl('/login');
  }

  isLogged(): boolean {
    try {
      return !this.helper.isTokenExpired(localStorage.getItem('token'));
    } catch (e) {
      return false;
    }
  }

  isAdmin(): boolean {
    const roles: RoleName[] = JSON.parse(localStorage.getItem('roles'));
    return roles.indexOf(RoleName.ROLE_ADMIN) !== -1;
  }

  selectWord(wordId: number): Observable<any> {
    return this.http.post(this.WORDS_URL + String(wordId), {});
  }

  deleteWord(wordId: number): Observable<any> {
    return this.http.put(this.WORDS_URL + String(wordId), {});
  }

  getWeeklyStatistics(): Observable<WeeklyStatisticsResponse> {
    return this.http.get<WeeklyStatisticsResponse>(this.USERS_URL + 'statistics');
  }

  changePassword(reqest: ChangePasswordRequest): Observable<any> {
    return this.http.put(this.USERS_URL, reqest);
  }

  selectWholeDictionary(dictionaryId: number): Observable<any> {
    return this.http.post(this.DICTIONARIES_URL + String(dictionaryId), {});
  }

  unselectWholeDictionary(dictionaryId: number): Observable<any> {
    return this.http.put(this.DICTIONARIES_URL + String(dictionaryId), {});
  }

}
