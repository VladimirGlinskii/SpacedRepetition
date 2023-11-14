package net.thumbtack.spaced.repetition.dto.request.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAnswerDtoRequest {
    private UUID uuid;
    private String translation;
}
