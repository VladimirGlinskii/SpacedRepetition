package net.thumbtack.spaced.repetition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.exception.ErrorCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
    private String field;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMsg();
        this.field = "";
    }
}
