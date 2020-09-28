package net.thumbtack.spaced.repetition.dto.response;

import net.thumbtack.spaced.repetition.exception.ErrorCode;

import java.util.Objects;

public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
    private String field;

    public ErrorResponse() {
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMsg();
        this.field = "";
    }

    public ErrorResponse(ErrorCode errorCode, String message, String field) {
        this.errorCode = errorCode;
        this.message = message;
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorResponse)) return false;
        ErrorResponse response = (ErrorResponse) o;
        return getErrorCode() == response.getErrorCode() &&
                Objects.equals(getMessage(), response.getMessage()) &&
                Objects.equals(getField(), response.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorCode(), getMessage(), getField());
    }
}
