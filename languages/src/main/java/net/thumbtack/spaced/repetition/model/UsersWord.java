package net.thumbtack.spaced.repetition.model;


import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_word")
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

    public UsersWord() {
    }

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Integer getLastInterval() {
        return lastInterval;
    }

    public void setLastInterval(Integer lastInterval) {
        this.lastInterval = lastInterval;
    }

    public UsersWordStatus getStatus() {
        return status;
    }

    public void setStatus(UsersWordStatus status) {
        this.status = status;
    }

    public LocalDateTime getNextDatetime() {
        return nextDatetime;
    }

    public void setNextDatetime(LocalDateTime nextDatetime) {
        this.nextDatetime = nextDatetime;
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
