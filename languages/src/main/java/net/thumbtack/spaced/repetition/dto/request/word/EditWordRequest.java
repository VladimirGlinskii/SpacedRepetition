package net.thumbtack.spaced.repetition.dto.request.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.utils.ErrorMessages;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditWordRequest {
    @NotBlank(message = ErrorMessages.EMPTY_WORD_NAME)
    private String word;

    @NotBlank(message = ErrorMessages.EMPTY_WORD_TRANSLATION)
    private String translation;
}
