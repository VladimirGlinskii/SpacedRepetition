package net.thumbtack.spaced.repetition.dto.response.statistics;

import java.time.LocalDate;
import java.util.Objects;

public class StatisticsResponse {
    private String username;
    private int correctAnswers;
    private int wrongAnswers;
    private LocalDate date;

    public StatisticsResponse(String username, int correctAnswers, int wrongAnswers, LocalDate date) {
        this.username = username;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.date = date;
    }

    public StatisticsResponse(String username, LocalDate date) {
        this(username, 0, 0, date);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatisticsResponse)) return false;
        StatisticsResponse that = (StatisticsResponse) o;
        return getCorrectAnswers() == that.getCorrectAnswers() &&
                getWrongAnswers() == that.getWrongAnswers() &&
                Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getCorrectAnswers(), getWrongAnswers(), getDate());
    }
}
