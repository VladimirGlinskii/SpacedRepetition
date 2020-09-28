package net.thumbtack.spaced.repetition.dto.request.word;

import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class AddWordRequest {
    @NotBlank(message = ErrorMessages.EMPTY_WORD_NAME)
    private String word;
    @NotBlank(message = ErrorMessages.EMPTY_WORD_TRANSLATION)
    private String translation;

    public AddWordRequest() {
    }

    public AddWordRequest(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddWordRequest)) return false;
        AddWordRequest request = (AddWordRequest) o;
        return Objects.equals(getWord(), request.getWord()) &&
                Objects.equals(getTranslation(), request.getTranslation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord(), getTranslation());
    }
}
