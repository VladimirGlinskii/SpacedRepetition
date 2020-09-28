import {Component, OnInit} from '@angular/core';
import {Exercise} from '../model/exercise';
import {Answer} from '../model/answer';
import {AnswerStatus} from '../model/enum/answer-status.enum';
import {ApiExerciseService} from '../service/api-exercise.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../model/dto/response/errors-response';
import {ErrorResponse} from '../model/dto/response/error-response';
import {CheckAnswerDtoResponse} from '../model/dto/response/exercise/check-answer-dto-response';
import {ApiWordService} from '../service/api-word.service';

@Component({
  selector: 'app-exercise-page',
  templateUrl: './exercise-page.component.html',
  styleUrls: ['./exercise-page.component.css']
})
export class ExercisePageComponent implements OnInit {
  exerciseModel: Exercise = {
    uuid: null,
    word: ''
  };
  answerModel: Answer = {
    uuid: null,
    translation: null
  };
  answerResponse: CheckAnswerDtoResponse = {
    status: null,
    translation: null
  };
  pronUrl: string = null;
  audio: HTMLAudioElement;
  enableAutoPron = true;

  constructor(private exerciseService: ApiExerciseService,
              private wordService: ApiWordService,
              private snackBar: MatSnackBar) {
    this.audio = new Audio();
  }

  ngOnInit(): void {
    this.getExercise();
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

  isAnswered(): boolean {
    return this.answerResponse.status !== null;
  }

  isCorrect(): boolean {
    return this.isAnswered() && this.answerResponse.status === AnswerStatus.CORRECT;
  }

  isWrong(): boolean {
    return this.isAnswered() && this.answerResponse.status === AnswerStatus.INCORRECT;
  }

  getExercise(): void {
    this.exerciseService.getExercise().subscribe(
      res => {
        this.answerModel.uuid = null;
        this.pronUrl = null;
        this.exerciseModel.uuid = res.uuid;
        this.exerciseModel.word = res.word;
        this.answerResponse.status = null;
        this.answerResponse.translation = null;
        if (this.enableAutoPron && res.word != null) {
          this.loadSound();
        }
      },
      error => this.handleError(error)
    );
  }

  checkAnswer(): void {
    this.answerModel.uuid = this.exerciseModel.uuid;
    this.exerciseService.checkAnswer(this.answerModel).subscribe(
      res => {
        this.answerResponse.status = res.status;
        this.answerResponse.translation = res.translation;
        this.answerModel.translation = null;
      },
      error => this.handleError(error)
    );
  }

  loadSound(): void {
    this.wordService.getWordPronunciationLink(this.exerciseModel.word).subscribe(
      res => {
        if (res.url != null) {
          this.pronUrl = res.url;
          const audio = new Audio();
          audio.src = res.url;
          audio.load();
          this.audio = audio;
          if (this.enableAutoPron) {
            this.playSound();
          }
        }
      },
      error => this.pronUrl = '[err]'
    );
  }

  playSound(): void {
    this.audio.play();
  }

  switchAutoPronunciation(): void {
    this.enableAutoPron = !this.enableAutoPron;
    if (this.enableAutoPron) {
      this.snackBar.open('Auto pronunciation enabled', null, {
        duration: 2000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar', 'green']
      });
      this.loadSound();
    } else {
      this.snackBar.open('Auto pronunciation disabled', null, {
        duration: 2000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar', 'green']
      });
    }
  }
}
