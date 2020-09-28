package net.thumbtack.spaced.repetition.dto.response.dictionary;

import java.util.Objects;

public class DictionaryDtoResponse {
    private int id;
    private String name;

    public DictionaryDtoResponse() {
    }

    public DictionaryDtoResponse(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictionaryDtoResponse)) return false;
        DictionaryDtoResponse that = (DictionaryDtoResponse) o;
        return getId() == that.getId() &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
