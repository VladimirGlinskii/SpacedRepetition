import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';
import {ErrorCode} from '../exception/error-code.enum';
import {ErrorResponse} from '../model/dto/response/error-response';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorsResponse} from '../model/dto/response/errors-response';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private snackBar: MatSnackBar) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request)
      .pipe(
        retry(1),
        catchError((error: HttpErrorResponse) => {
          let errorsResponse: ErrorsResponse = {
            errors: null
          };
          if (error.error instanceof ErrorEvent) {
            errorsResponse.errors = [{
              errorCode: ErrorCode.CLIENT_SIDE_ERROR,
              message: ErrorCode[ErrorCode.CLIENT_SIDE_ERROR],
              field: ''
            }];
          } else {
            // server-side error
            errorsResponse = error.error as ErrorsResponse;
            if (errorsResponse.errors == null) {
              this.snackBar.open('Unknown server error', null, {
                duration: 3000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar']
              });
            }
          }
          return throwError(errorsResponse);
        })
      );
  }
}
