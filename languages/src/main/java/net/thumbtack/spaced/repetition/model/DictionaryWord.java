package net.thumbtack.spaced.repetition.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "word_dictionary")
@Getter
@Setter
@NoArgsConstructor
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

    public DictionaryWord(Dictionary dictionary, Word word) {
        this.dictionary = dictionary;
        this.word = word;
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
