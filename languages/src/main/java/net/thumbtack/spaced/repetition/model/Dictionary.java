package net.thumbtack.spaced.repetition.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "dictionary")
public class Dictionary implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "word_dictionary",
            joinColumns = @JoinColumn(name = "dictionary_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id"))
    private Set<Word> words = new HashSet<>();

    public Dictionary() {
    }

    public Dictionary(String name) {
        this.name = name;
    }

    public Dictionary(String name, Set<Word> words) {
        this(name);
        this.words = words;
    }

    public Dictionary(int id, String name, Set<Word> words) {
        this(name, words);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Word> getWords() {
        return words;
    }

    public void setWords(Set<Word> words) {
        this.words = words;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dictionary)) return false;
        Dictionary that = (Dictionary) o;
        return getId() == that.getId() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getWords(), that.getWords());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getWords());
    }
}
