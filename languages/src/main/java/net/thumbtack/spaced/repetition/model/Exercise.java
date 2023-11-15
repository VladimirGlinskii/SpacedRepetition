package net.thumbtack.spaced.repetition.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.spaced.repetition.model.enums.ExerciseStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@NoArgsConstructor
public class Exercise implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_word_id")
    private UsersWord usersWord;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private ExerciseStatus status;

    @Column(name = "answer_datetime")
    private LocalDateTime answerDatetime;

    public Exercise(UsersWord usersWord, User user) {
        this.usersWord = usersWord;
        this.user = user;
        status = ExerciseStatus.NO_ANSWER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(getUuid(), exercise.getUuid()) &&
                Objects.equals(getUser(), exercise.getUser()) &&
                Objects.equals(getUsersWord(), exercise.getUsersWord()) &&
                getStatus() == exercise.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getUser(), getUsersWord(), getStatus());
    }
}
