package net.thumbtack.spaced.repetition.exception;

public class ServerRuntimeException extends RuntimeException {
    private ErrorCode errorCode;

    public ServerRuntimeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
