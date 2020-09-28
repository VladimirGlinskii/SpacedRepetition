import {AnswerStatus} from '../../../enum/answer-status.enum';

export interface CheckAnswerDtoResponse {
  status: AnswerStatus;
  translation: string[];
}
