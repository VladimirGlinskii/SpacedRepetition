package net.thumbtack.spaced.repetition.dto.request.exercise;

import java.util.Objects;
import java.util.UUID;

public class CheckAnswerDtoRequest {
    private UUID uuid;
    private String translation;


    public CheckAnswerDtoRequest() {
    }

    public CheckAnswerDtoRequest(UUID uuid, String translation) {
        this.uuid = uuid;
        this.translation = translation;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
        if (!(o instanceof CheckAnswerDtoRequest)) return false;
        CheckAnswerDtoRequest that = (CheckAnswerDtoRequest) o;
        return Objects.equals(getUuid(), that.getUuid()) &&
                Objects.equals(getTranslation(), that.getTranslation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getTranslation());
    }
}
