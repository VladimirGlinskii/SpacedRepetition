package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Integer> {

    Optional<Word> findByWordIgnoreCase(String word);

    @Query("SELECT w FROM DictionaryWord dw " +
            "LEFT JOIN Word w ON dw.word.id = w.id " +
            "WHERE dw.dictionary.id = :dictionaryId AND lower(w.word) LIKE lower(concat(:filter, '%'))")
    Page<Word> getDictionaryWordsStartsWithIgnoreCase(int dictionaryId, Pageable pageable, String filter);

    Page<Word> findAllByWordStartsWithIgnoreCase(Pageable pageable, String filter);

}
