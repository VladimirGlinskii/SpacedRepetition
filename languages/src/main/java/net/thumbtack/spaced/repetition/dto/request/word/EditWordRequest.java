package net.thumbtack.spaced.repetition.dto.request.word;

import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class EditWordRequest {
    @NotBlank(message = ErrorMessages.EMPTY_WORD_NAME)
    private String word;
    @NotBlank(message = ErrorMessages.EMPTY_WORD_TRANSLATION)
    private String translation;

    public EditWordRequest() {
    }

    public EditWordRequest(String word, String translation) {
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
        if (!(o instanceof EditWordRequest)) return false;
        EditWordRequest that = (EditWordRequest) o;
        return Objects.equals(getWord(), that.getWord()) &&
                Objects.equals(getTranslation(), that.getTranslation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord(), getTranslation());
    }
}
