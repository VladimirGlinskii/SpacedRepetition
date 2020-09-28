package net.thumbtack.spaced.repetition.dto.request.dictionary;

import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class RenameDictionaryRequest {
    @NotBlank(message = ErrorMessages.EMPTY_DICTIONARY_NAME)
    private String name;

    public RenameDictionaryRequest() {
    }

    public RenameDictionaryRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RenameDictionaryRequest)) return false;
        RenameDictionaryRequest that = (RenameDictionaryRequest) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
