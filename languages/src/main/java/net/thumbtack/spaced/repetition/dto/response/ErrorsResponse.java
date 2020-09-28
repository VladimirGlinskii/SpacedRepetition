package net.thumbtack.spaced.repetition.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ErrorsResponse {
    private List<ErrorResponse> errors;

    public ErrorsResponse() {
    }

    public ErrorsResponse(ErrorResponse error) {
        this.errors = new ArrayList<ErrorResponse>() {{
            add(error);
        }};
    }

    public ErrorsResponse(List<ErrorResponse> errors) {
        this.errors = errors;
    }

    public List<ErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResponse> errors) {
        this.errors = errors;
    }
}
