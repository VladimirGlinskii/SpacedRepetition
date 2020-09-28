package net.thumbtack.spaced.repetition.controller;

import net.thumbtack.spaced.repetition.dto.response.ErrorResponse;
import net.thumbtack.spaced.repetition.dto.response.ErrorsResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorsResponse> handleException(Exception e) {
        return new ResponseEntity<>(
                new ErrorsResponse(new ErrorResponse(ErrorCode.UNKNOWN_SERVER_ERROR)),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ServerRuntimeException.class})
    public ResponseEntity<ErrorsResponse>
    handleServerRuntimeException(ServerRuntimeException e) {
        return new ResponseEntity<>(new ErrorsResponse(new ErrorResponse(e.getErrorCode())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<ErrorsResponse> handleServerRuntimeException(
            BadCredentialsException e) {
        return new ResponseEntity<>(
                new ErrorsResponse(new ErrorResponse(ErrorCode.INVALID_USERNAME_OR_PASSWORD)),
                HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorsResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) ->
                errors.add(new ErrorResponse(ErrorCode.FIELD_NOT_VALID,
                        error.getDefaultMessage(),
                        ((FieldError) error).getField())));
        return new ResponseEntity<>(new ErrorsResponse(errors), HttpStatus.BAD_REQUEST);
    }

}
