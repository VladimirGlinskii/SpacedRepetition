package net.thumbtack.spaced.repetition.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDtoResponse {
    private UUID uuid;
    private String word;
}
