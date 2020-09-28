import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {APP_CONFIG, AppConfig} from '../app-config';
import {Observable} from 'rxjs';
import {Exercise} from '../model/exercise';
import {CheckAnswerDtoResponse} from '../model/dto/response/exercise/check-answer-dto-response';
import {Answer} from '../model/answer';

@Injectable({
  providedIn: 'root'
})
export class ApiExerciseService {
  private BASIC_URL = this.config.apiEndpoint + 'exercises';

  constructor(private http: HttpClient,
              @Inject(APP_CONFIG) private config: AppConfig) {}

  getExercise(): Observable<Exercise> {
    return this.http.get<Exercise>(this.BASIC_URL);
  }

  checkAnswer(answer: Answer): Observable<CheckAnswerDtoResponse> {
    return this.http.put<CheckAnswerDtoResponse>(this.BASIC_URL, answer);
  }
}
