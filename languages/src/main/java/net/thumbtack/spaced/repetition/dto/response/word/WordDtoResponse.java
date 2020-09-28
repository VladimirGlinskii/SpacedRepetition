package net.thumbtack.spaced.repetition.dto.response.word;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.thumbtack.spaced.repetition.utils.JsonRawValueDeserializer;

import java.util.Objects;

@JsonSerialize
@JsonDeserialize
public class WordDtoResponse {
    private int id;
    private String name;
    @JsonRawValue
    @JsonDeserialize(using = JsonRawValueDeserializer.class)
    private String translation;
    private boolean selected;

    public WordDtoResponse() {
    }

    public WordDtoResponse(int id, String name, String translation, boolean selected) {
        this.id = id;
        this.name = name;
        this.translation = translation;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordDtoResponse)) return false;
        WordDtoResponse that = (WordDtoResponse) o;
        return getId() == that.getId() &&
                isSelected() == that.isSelected() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getTranslation(), that.getTranslation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getTranslation(), isSelected());
    }
}
