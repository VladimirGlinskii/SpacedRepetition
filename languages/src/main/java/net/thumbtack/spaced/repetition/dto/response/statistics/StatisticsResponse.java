package net.thumbtack.spaced.repetition.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {
    private String username;
    private int correctAnswers;
    private int wrongAnswers;
    private LocalDate date;

    public StatisticsResponse(String username, LocalDate date) {
        this(username, 0, 0, date);
    }
}
