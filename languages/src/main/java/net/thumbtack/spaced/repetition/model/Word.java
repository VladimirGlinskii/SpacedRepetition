package net.thumbtack.spaced.repetition.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "word")
public class Word implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "word")
    private String word;

    @Column(name = "translation")
    private String translation;

    @ManyToMany(mappedBy = "words", fetch = FetchType.LAZY)
    private Set<Dictionary> dictionaries;

    public Word() {
    }

    public Word(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public Word(String word, String translation, Set<Dictionary> dictionaries) {
        this(word, translation);
        this.dictionaries = dictionaries;
    }

    public Word(int id, String word, String translation) {
        this(word, translation);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Set<Dictionary> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Set<Dictionary> dictionaries) {
        this.dictionaries = dictionaries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;
        Word word1 = (Word) o;
        return getId() == word1.getId() &&
                Objects.equals(getWord(), word1.getWord()) &&
                Objects.equals(getTranslation(), word1.getTranslation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWord(), getTranslation());
    }
}
