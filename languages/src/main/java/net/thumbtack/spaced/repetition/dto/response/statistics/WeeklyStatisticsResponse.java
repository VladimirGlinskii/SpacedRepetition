package net.thumbtack.spaced.repetition.dto.response.statistics;

import net.thumbtack.spaced.repetition.model.Statistics;
import net.thumbtack.spaced.repetition.model.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

public class WeeklyStatisticsResponse {
    private StatisticsResponse[] weeklyStatistics;

    public WeeklyStatisticsResponse(List<Statistics> statisticsList, User user) {
        LocalDate now = LocalDate.now();
        weeklyStatistics = new StatisticsResponse[7];

        for (Statistics s : statisticsList) {
            weeklyStatistics[6 - Period.between(s.getDate(), now).getDays()] =
                    new StatisticsResponse(
                            user.getUsername(),
                            s.getCorrectAnswers(),
                            s.getWrongAnswers(),
                            s.getDate()
                    );
        }
        for (int i = 0; i < 7; i++) {
            if (weeklyStatistics[i] == null) {
                weeklyStatistics[i] = new StatisticsResponse(user.getUsername(), now.minusDays(6 - i));
            }
        }
    }

    public List<StatisticsResponse> getWeeklyStatistics() {
        return Arrays.asList(weeklyStatistics);
    }

}
