package net.thumbtack.spaced.repetition.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_word")
@Getter
@Setter
@NoArgsConstructor
public class UsersWord implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "last_interval")
    private Integer lastInterval;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private UsersWordStatus status;

    @Column(name = "next_datetime")
    private LocalDateTime nextDatetime;

    public UsersWord(User user, Word word) {
        this.user = user;
        this.word = word;
        lastInterval = 0;
        status = UsersWordStatus.ACTIVE;
        nextDatetime = LocalDateTime.now();
    }

    public UsersWord(Integer id, User user, Word word, Integer lastInterval,
                     UsersWordStatus status, LocalDateTime nextDatetime) {
        this.id = id;
        this.user = user;
        this.word = word;
        this.lastInterval = lastInterval;
        this.status = status;
        this.nextDatetime = nextDatetime;
    }

    public UsersWord(Word word, UsersWordStatus status) {
        this.word = word;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersWord)) return false;
        UsersWord usersWord = (UsersWord) o;
        return Objects.equals(getId(), usersWord.getId()) &&
                Objects.equals(getUser(), usersWord.getUser()) &&
                Objects.equals(getWord(), usersWord.getWord()) &&
                Objects.equals(getLastInterval(), usersWord.getLastInterval()) &&
                getStatus() == usersWord.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getWord(), getLastInterval(), getStatus());
    }
}
