package net.thumbtack.spaced.repetition.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "word_dictionary")
public class DictionaryWord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    public DictionaryWord() {
    }

    public DictionaryWord(Dictionary dictionary, Word word) {
        this.dictionary = dictionary;
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictionaryWord)) return false;
        DictionaryWord that = (DictionaryWord) o;
        return getId() == that.getId() &&
                Objects.equals(getWord(), that.getWord()) &&
                Objects.equals(getDictionary(), that.getDictionary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWord(), getDictionary());
    }
}
