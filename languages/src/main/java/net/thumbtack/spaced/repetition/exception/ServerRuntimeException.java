package net.thumbtack.spaced.repetition.exception;

import lombok.Getter;

@Getter
public class ServerRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;

    public ServerRuntimeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
