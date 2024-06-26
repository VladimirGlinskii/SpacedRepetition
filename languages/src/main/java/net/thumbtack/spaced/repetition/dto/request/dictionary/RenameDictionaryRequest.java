package net.thumbtack.spaced.repetition.dto.request.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenameDictionaryRequest {
    @NotBlank(message = ErrorMessages.EMPTY_DICTIONARY_NAME)
    private String name;
}
