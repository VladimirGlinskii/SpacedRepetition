package net.thumbtack.spaced.repetition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorsResponse {
    private List<ErrorResponse> errors;

    public ErrorsResponse(ErrorResponse error) {
        this.errors = new ArrayList<ErrorResponse>() {{
            add(error);
        }};
    }
}
