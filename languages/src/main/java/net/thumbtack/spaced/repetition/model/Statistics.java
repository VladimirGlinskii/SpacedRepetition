package net.thumbtack.spaced.repetition.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "statistics")
public class Statistics {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "correct_answers")
    private int correctAnswers;

    @Column(name = "wrong_answers")
    private int wrongAnswers;

    @Column(name = "date")
    private LocalDate date;

    public Statistics() {
    }

    public Statistics(int id, User user, int correctAnswers, int wrongAnswers, LocalDate date) {
        this.id = id;
        this.user = user;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.date = date;
    }

    public Statistics(User user, LocalDate date) {
        this(0, user, 0, 0, date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void increaseCorrectAnswers() {
        correctAnswers++;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public void increaseWrongAnswers() {
        wrongAnswers++;
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
        if (!(o instanceof Statistics)) return false;
        Statistics that = (Statistics) o;
        return getId() == that.getId() &&
                getCorrectAnswers() == that.getCorrectAnswers() &&
                getWrongAnswers() == that.getWrongAnswers() &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getCorrectAnswers(), getWrongAnswers(), getDate());
    }
}
