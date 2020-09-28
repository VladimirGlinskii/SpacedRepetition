package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.DictionaryWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryWordRepository extends JpaRepository<DictionaryWord, Integer> {

    Page<DictionaryWord> findAllByDictionaryId(int dictionaryId, Pageable pageable);

}
