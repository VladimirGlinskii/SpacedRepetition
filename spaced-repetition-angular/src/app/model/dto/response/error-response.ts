import {ErrorCode} from '../../../exception/error-code.enum';

export interface ErrorResponse {
  errorCode: ErrorCode;
  message: string;
  field: string;
}
