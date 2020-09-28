package net.thumbtack.spaced.repetition.dto.response.exercise;

import java.util.UUID;

public class ExerciseDtoResponse {
    private UUID uuid;
    private String word;

    public ExerciseDtoResponse() {
    }

    public ExerciseDtoResponse(UUID uuid, String word) {
        this.uuid = uuid;
        this.word = word;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

}
