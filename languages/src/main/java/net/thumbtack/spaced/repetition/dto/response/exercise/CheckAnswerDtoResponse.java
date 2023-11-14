package net.thumbtack.spaced.repetition.dto.response.exercise;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.dto.enums.AnswerStatus;
import net.thumbtack.spaced.repetition.utils.JsonRawValueDeserializer;

@JsonSerialize
@JsonDeserialize
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAnswerDtoResponse {
    private AnswerStatus status;
    @JsonRawValue
    @JsonDeserialize(using = JsonRawValueDeserializer.class)
    private String translation;
}
