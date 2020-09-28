package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

public interface UsersWordRepository extends JpaRepository<UsersWord, Integer> {

    UsersWord findTopByUserIdAndStatusOrderByNextDatetimeAsc(int userId, UsersWordStatus status);

    UsersWord findByUserIdAndWordId(int userId, int wordId);

    Set<UsersWord> findByUserId(int userId);

    @Query("SELECT new UsersWord(w, uw.status) FROM Word w " +
            "LEFT JOIN UsersWord uw ON w.id = uw.word.id AND uw.user.id = :userId " +
            "WHERE lower(w.word) LIKE lower(concat(:filter, '%'))")
    Page<UsersWord> getAllWordsWithUserStatusStartsWithIgnoreCase(int userId,
                                                                  Pageable pageable,
                                                                  String filter);

    @Query("SELECT new UsersWord(dw.word, uw.status) FROM DictionaryWord dw " +
            "LEFT JOIN UsersWord uw ON dw.word.id = uw.word.id AND uw.user.id = :userId " +
            "WHERE dw.dictionary.id = :dictionaryId AND lower(dw.word.word) LIKE lower(concat(:filter, '%'))")
    Page<UsersWord> getDictionaryWordsWithUserStatusStartsWithIgnoreCase(int dictionaryId,
                                                                         int userId,
                                                                         Pageable pageable,
                                                                         String filter);

    @Query("SELECT dw.word FROM DictionaryWord dw " +
            "WHERE dw.word.id NOT IN (SELECT uw.word.id FROM UsersWord uw WHERE uw.user.id = :userId)" +
            "AND dw.dictionary.id = :dictionaryId")
    List<Word> getDictionaryUntrackedWords(int dictionaryId, int userId);

    @Transactional
    @Modifying
    @Query("UPDATE UsersWord uw SET uw.status = 'ACTIVE' " +
            "WHERE uw.word.id IN (SELECT dw.word.id FROM DictionaryWord dw WHERE dw.dictionary.id = :dictionaryId) " +
            "AND uw.status = 'DISABLED' AND uw.user.id = :userId")
    void selectUnselectedDictionaryWords(int dictionaryId, int userId);

    @Transactional
    @Modifying
    @Query("UPDATE UsersWord uw SET uw.status = 'DISABLED' " +
            "WHERE uw.word.id IN (SELECT dw.word.id FROM DictionaryWord dw WHERE dw.dictionary.id = :dictionaryId) " +
            "AND uw.status = 'ACTIVE' AND uw.user.id = :userId")
    void unselectSelectedDictionaryWords(int dictionaryId, int userId);
}
