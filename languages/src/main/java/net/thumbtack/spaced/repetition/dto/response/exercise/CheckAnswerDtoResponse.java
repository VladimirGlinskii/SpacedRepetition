package net.thumbtack.spaced.repetition.dto.response.exercise;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.thumbtack.spaced.repetition.dto.enums.AnswerStatus;
import net.thumbtack.spaced.repetition.utils.JsonRawValueDeserializer;

@JsonSerialize
@JsonDeserialize
public class CheckAnswerDtoResponse {
    private AnswerStatus status;
    @JsonRawValue
    @JsonDeserialize(using = JsonRawValueDeserializer.class)
    private String translation;

    public CheckAnswerDtoResponse() {
    }

    public CheckAnswerDtoResponse(AnswerStatus status, String translation) {
        this.status = status;
        this.translation = translation;
    }

    public AnswerStatus getStatus() {
        return status;
    }

    public void setStatus(AnswerStatus status) {
        this.status = status;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
