package net.thumbtack.spaced.repetition.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "word")
@Getter
@Setter
@NoArgsConstructor
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
