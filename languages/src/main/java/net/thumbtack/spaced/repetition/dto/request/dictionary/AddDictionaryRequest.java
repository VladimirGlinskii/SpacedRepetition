package net.thumbtack.spaced.repetition.dto.request.dictionary;

import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class AddDictionaryRequest {
    @NotBlank(message = ErrorMessages.EMPTY_DICTIONARY_NAME)
    private String name;

    public AddDictionaryRequest() {
    }

    public AddDictionaryRequest(String name) {
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
        if (!(o instanceof AddDictionaryRequest)) return false;
        AddDictionaryRequest request = (AddDictionaryRequest) o;
        return Objects.equals(getName(), request.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
