package net.thumbtack.spaced.repetition.mock.service;

import net.thumbtack.spaced.repetition.dto.request.word.AddWordRequest;
import net.thumbtack.spaced.repetition.dto.request.word.EditWordRequest;
import net.thumbtack.spaced.repetition.dto.response.word.*;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.model.Dictionary;
import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import net.thumbtack.spaced.repetition.service.WordService;
import net.thumbtack.spaced.repetition.utils.OffsetBasedPageable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class WordServiceTest extends ServiceTestBase {

    @Autowired
    private WordService wordService;

    @Test
    void whenDictionaryIdIsZero_getDictionaryWordsForUser_shouldReturnWordsOnPage0WithCorrectSelectedFlag() {
        mockSecurityContextHolderForUser(standardUser);
        List<UsersWord> wordsList = Arrays.asList(standardUsersWords);
        wordsList.sort(Comparator.comparing(w -> w.getWord().getWord()));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word.word");
        Mockito.when(usersWordRepository
                .getAllWordsWithUserStatusStartsWithIgnoreCase(standardUser.getId(), pageable, "filter"))
                .thenReturn(new PageImpl<UsersWord>(wordsList.subList(0, properties.getMaxPageSize()),
                        pageable, standardUsersWords.length));
        GetDictionaryWordsDtoResponse response =
                wordService.getDictionaryWordsForUser(0, 0, properties.getMaxPageSize(), "filter");
        Mockito.verify(
                usersWordRepository, Mockito.times(1))
                .getAllWordsWithUserStatusStartsWithIgnoreCase(standardUser.getId(), pageable, "filter");
        Mockito.verify(
                usersWordRepository, Mockito.never())
                .getDictionaryWordsWithUserStatusStartsWithIgnoreCase(
                        Mockito.anyInt(), Mockito.anyInt(),
                        Mockito.any(), Mockito.any());
        Assertions.assertTrue(response.getWords().containsAll(
                wordsList.stream().limit(properties.getMaxPageSize())
                        .map(w -> new WordDtoResponse(w.getId(), w.getWord().getWord(),
                                w.getWord().getTranslation(),
                                w.getStatus() == UsersWordStatus.ACTIVE))
                        .collect(Collectors.toSet())
        ));
    }

    @Test
    void whenDictionaryIdIsNotZero_getDictionaryWordsForUser_shouldReturnDictionaryWordsOnPage0WithCorrectSelectedFlag() {
        mockSecurityContextHolderForUser(standardUser);
        List<UsersWord> wordsList = Arrays.asList(standardUsersWords);
        wordsList.sort(Comparator.comparing(w -> w.getWord().getWord()));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word.word");
        Mockito.when(usersWordRepository
                .getDictionaryWordsWithUserStatusStartsWithIgnoreCase(1, standardUser.getId(), pageable, "filter"))
                .thenReturn(new PageImpl<UsersWord>(wordsList.subList(0, properties.getMaxPageSize()),
                        pageable, standardUsersWords.length));
        GetDictionaryWordsDtoResponse response =
                wordService.getDictionaryWordsForUser(1, 0, properties.getMaxPageSize(), "filter");
        Mockito.verify(
                usersWordRepository, Mockito.never())
                .getAllWordsWithUserStatusStartsWithIgnoreCase(Mockito.anyInt(), Mockito.any(), Mockito.any());
        Mockito.verify(
                usersWordRepository, Mockito.times(1))
                .getDictionaryWordsWithUserStatusStartsWithIgnoreCase(1, standardUser.getId(), pageable, "filter");
        Assertions.assertTrue(response.getWords().containsAll(
                wordsList.stream().limit(pageable.getPageSize())
                        .map(w -> new WordDtoResponse(w.getId(), w.getWord().getWord(),
                                w.getWord().getTranslation(),
                                w.getStatus() == UsersWordStatus.ACTIVE))
                        .collect(Collectors.toSet())
        ));
    }

    @Test
    void whenPageSizeTooBig_getDictionaryWordsForUser_shouldSetMaxPageSize() {
        int pageSize = properties.getMaxPageSize() + 1;
        mockSecurityContextHolderForUser(standardUser);
        List<UsersWord> wordsList = Arrays.asList(standardUsersWords);
        wordsList.sort(Comparator.comparing(w -> w.getWord().getWord()));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word.word");
        Mockito.when(usersWordRepository
                .getAllWordsWithUserStatusStartsWithIgnoreCase(standardUser.getId(), pageable, ""))
                .thenReturn(new PageImpl<UsersWord>(wordsList.subList(0, properties.getMaxPageSize()),
                        pageable, standardUsersWords.length));
        wordService.getDictionaryWordsForUser(0, 0, pageSize, "");
        Mockito.verify(
                usersWordRepository, Mockito.times(1))
                .getAllWordsWithUserStatusStartsWithIgnoreCase(standardUser.getId(), pageable, "");

    }

    @Test
    void whenPageSizeIsNegative_getDictionaryWordsForUser_shouldSetMaxPageSize() {
        int pageSize = -1;
        mockSecurityContextHolderForUser(standardUser);
        List<UsersWord> wordsList = Arrays.asList(standardUsersWords);
        wordsList.sort(Comparator.comparing(w -> w.getWord().getWord()));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word.word");
        Mockito.when(usersWordRepository
                .getAllWordsWithUserStatusStartsWithIgnoreCase(standardUser.getId(), pageable, ""))
                .thenReturn(new PageImpl<UsersWord>(wordsList.subList(0, properties.getMaxPageSize()),
                        pageable, standardUsersWords.length));
        wordService.getDictionaryWordsForUser(0, 0, pageSize, "");
        Mockito.verify(
                usersWordRepository, Mockito.times(1))
                .getAllWordsWithUserStatusStartsWithIgnoreCase(standardUser.getId(), pageable, "");

    }

    @Test
    void whenWordNotExists_addWord_shouldSaveWord() {
        String transl = getSimpleTranslation(words[0]);
        AddWordRequest request = new AddWordRequest(words[0].getWord(), transl);
        AddWordResponse response = wordService.addWord(request, 0);
        Mockito.verify(wordRepository, Mockito.times(1))
                .save(new Word(words[0].getWord(), words[0].getTranslation()));
        Assertions.assertEquals(request.getWord(), response.getWord());
    }

    @Test
    void whenWordExists_addWord_shouldAddTranslationAndSaveWord() {
        String transl = getSimpleTranslation(words[0]);
        Mockito.when(wordRepository.findByWordIgnoreCase(words[0].getWord()))
                .thenReturn(Optional.of(words[0]));
        AddWordRequest request = new AddWordRequest(words[0].getWord(), transl + ", transl1.2");
        AddWordResponse response = wordService.addWord(request, 0);
        ArgumentCaptor<Word> savedWord = ArgumentCaptor.forClass(Word.class);
        Mockito.verify(wordRepository, Mockito.times(1))
                .save(savedWord.capture());
        Set<String> translations = getTranslationAsSet(savedWord.getValue());
        Assertions.assertTrue(translations.containsAll(Arrays.asList(transl, "transl1.2")));
        Assertions.assertEquals(2, translations.size());
        Assertions.assertEquals(request.getWord(), response.getWord());

    }

    @Test
    void whenWordNotExists_editWord_shouldThrowException() {
        String transl = getSimpleTranslation(words[0]);
        EditWordRequest request = new EditWordRequest(words[0].getWord(), transl);
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenThrow(new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND));
        try {
            wordService.editWord(request, words[0].getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WORD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    void whenWordExists_editWord_shouldUpdateWord() {
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.of(words[0]));
        EditWordRequest request = new EditWordRequest(words[0].getWord() + "_edited", "transl1.2");
        wordService.editWord(request, words[0].getId());
        ArgumentCaptor<Word> savedWord = ArgumentCaptor.forClass(Word.class);
        Mockito.verify(wordRepository, Mockito.times(1))
                .save(savedWord.capture());
        Set<String> translations = getTranslationAsSet(savedWord.getValue());
        Assertions.assertTrue(translations.contains("transl1.2"));
        Assertions.assertEquals(1, translations.size());
        Assertions.assertEquals(request.getWord(), savedWord.getValue().getWord());
    }

    @Test
    void whenWordNotExists_deleteWord_shouldThrowException() {
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenThrow(new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND));
        try {
            wordService.deleteWord(words[0].getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WORD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    void whenWordExists_deleteWord_shouldDeleteWord() {
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.of(words[0]));
        wordService.deleteWord(words[0].getId());
        Mockito.verify(wordRepository, Mockito.times(1))
                .delete(words[0]);
    }

    @Test
    void getWordsForDictionaryPage0_shouldReturnWordsOnPage0WithCorrectSelectedFlag() {
        Dictionary dictionary = new Dictionary(1, "dictionary",
                new HashSet<Word>() {{
                    add(words[0]);
                    add(words[3]);
                    add(words[5]);
                }});
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        List<Word> wordsList = Arrays.asList(words);
        wordsList.sort(Comparator.comparing(Word::getWord));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word");
        Mockito.when(wordRepository.findAllByWordStartsWithIgnoreCase(pageable, "filter")).thenReturn(
                new PageImpl<Word>(wordsList.subList(0, properties.getMaxPageSize()),
                        pageable, words.length)
        );
        List<WordDtoResponse> expectedList = wordsList
                .stream()
                .limit(properties.getMaxPageSize())
                .map(w -> new WordDtoResponse(
                        w.getId(),
                        w.getWord(),
                        w.getTranslation(),
                        dictionary.getWords().contains(w)))
                .collect(Collectors.toList());
        GetWordsForDictionaryResponse response =
                wordService.getWordsForDictionary(1, 0, properties.getMaxPageSize(), "filter");
        Assertions.assertEquals(expectedList, response.getWords());
    }

    @Test
    void getWordsForDictionaryPage1_shouldReturnWordsOnPage1WithCorrectSelectedFlag() {
        Dictionary dictionary = new Dictionary(1, "dictionary",
                new HashSet<Word>() {{
                    add(words[0]);
                    add(words[3]);
                    add(words[5]);
                }});
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        List<Word> wordsList = Arrays.asList(words);
        wordsList.sort(Comparator.comparing(Word::getWord));
        Pageable pageable = new OffsetBasedPageable(
                properties.getMaxPageSize(),
                properties.getMaxPageSize(),
                Sort.Direction.ASC, "word");
        Mockito.when(wordRepository.findAllByWordStartsWithIgnoreCase(pageable, "filter")).thenReturn(
                new PageImpl<Word>(wordsList.subList(
                        properties.getMaxPageSize(),
                        properties.getMaxPageSize() * 2),
                        pageable, words.length)
        );
        List<WordDtoResponse> expectedList = wordsList
                .stream()
                .skip(properties.getMaxPageSize())
                .limit(properties.getMaxPageSize())
                .map(w -> new WordDtoResponse(
                        w.getId(),
                        w.getWord(),
                        w.getTranslation(),
                        dictionary.getWords().contains(w)))
                .collect(Collectors.toList());
        GetWordsForDictionaryResponse response = wordService
                .getWordsForDictionary(1, 1, properties.getMaxPageSize(), "filter");
        Assertions.assertEquals(expectedList, response.getWords());
    }

    @Test
    void whenDictionaryNotExists_getWordsForDictionary_shouldThrowException() {
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.empty());
        try {
            wordService.getWordsForDictionary(1, 0, properties.getMaxPageSize(), "filter");
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, e.getErrorCode());
        }
    }

    @Test
    void whenPageSizeTooBig_getWordsForDictionary_shouldSetMaxPageSize() {
        Dictionary dictionary = new Dictionary(1, "dictionary",
                new HashSet<Word>() {{
                    add(words[0]);
                    add(words[3]);
                    add(words[5]);
                }});
        int pageSize = properties.getMaxPageSize() + 1;
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word");
        Mockito.when(wordRepository.findAllByWordStartsWithIgnoreCase(pageable, "filter")).thenReturn(
                new PageImpl<Word>(Arrays.asList(words), pageable, words.length)
        );
        wordService.getWordsForDictionary(dictionary.getId(), 0, pageSize, "filter");
        Mockito.verify(wordRepository, Mockito.times(1))
                .findAllByWordStartsWithIgnoreCase(pageable, "filter");
    }

    @Test
    void whenPageSizeIsNegative_getWordsForDictionary_shouldSetMaxPageSize() {
        Dictionary dictionary = new Dictionary(1, "dictionary",
                new HashSet<Word>() {{
                    add(words[0]);
                    add(words[3]);
                    add(words[5]);
                }});
        int pageSize = -1;
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word");
        Mockito.when(wordRepository.findAllByWordStartsWithIgnoreCase(pageable, "filter")).thenReturn(
                new PageImpl<Word>(Arrays.asList(words), pageable, words.length)
        );
        wordService.getWordsForDictionary(dictionary.getId(), 0, pageSize, "filter");
        Mockito.verify(wordRepository, Mockito.times(1))
                .findAllByWordStartsWithIgnoreCase(pageable, "filter");
    }

    @Test
    void getDictionaryWordsPage0_shouldReturnWordsOnPage0WithCorrectSelectedFlag() {
        Dictionary dictionary = new Dictionary(1, "dictionary",
                new HashSet<Word>() {{
                    add(words[0]);
                    add(words[3]);
                    add(words[5]);
                }});
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        List<Word> wordsList = Arrays.asList(words);
        wordsList.sort(Comparator.comparing(Word::getWord));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word.word");
        Mockito.when(wordRepository.getDictionaryWordsStartsWithIgnoreCase(dictionary.getId(), pageable, "filter"))
                .thenReturn(new PageImpl<Word>(
                        wordsList.subList(0, properties.getMaxPageSize()),
                        pageable, words.length)
                );
        List<WordDtoResponse> expectedList = wordsList
                .stream()
                .limit(properties.getMaxPageSize())
                .map(w -> new WordDtoResponse(
                        w.getId(),
                        w.getWord(),
                        w.getTranslation(),
                        true))
                .collect(Collectors.toList());
        GetWordsForDictionaryResponse response =
                wordService.getDictionaryWords(dictionary.getId(), 0, properties.getMaxPageSize(), "filter");
        Assertions.assertEquals(expectedList, response.getWords());
    }

    @Test
    void whenDictionaryNotExists_getDictionaryWords_shouldThrowException() {
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.empty());
        try {
            wordService.getDictionaryWords(1, 0, properties.getMaxPageSize(), "filter");
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, e.getErrorCode());
        }
    }

    @Test
    void whenPageSizeTooBig_getDictionaryWords_shouldSetMaxPageSize() {
        Dictionary dictionary = new Dictionary(1, "dictionary",
                new HashSet<Word>() {{
                    add(words[0]);
                    add(words[3]);
                    add(words[5]);
                }});
        int pageSize = properties.getMaxPageSize() + 1;
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word.word");
        Mockito.when(wordRepository.getDictionaryWordsStartsWithIgnoreCase(dictionary.getId(), pageable, "filter"))
                .thenReturn(new PageImpl<Word>(Arrays.asList(words), pageable, words.length)
                );
        wordService.getDictionaryWords(dictionary.getId(), 0, pageSize, "filter");
        Mockito.verify(wordRepository, Mockito.times(1))
                .getDictionaryWordsStartsWithIgnoreCase(dictionary.getId(), pageable, "filter");
    }

    @Test
    void whenPageSizeIsNegative_getDictionaryWords_shouldSetMaxPageSize() {
        Dictionary dictionary = new Dictionary(1, "dictionary",
                new HashSet<Word>() {{
                    add(words[0]);
                    add(words[3]);
                    add(words[5]);
                }});
        int pageSize = -1;
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word.word");
        Mockito.when(wordRepository.getDictionaryWordsStartsWithIgnoreCase(dictionary.getId(), pageable, "filter"))
                .thenReturn(
                        new PageImpl<Word>(Arrays.asList(words), pageable, words.length)
                );
        wordService.getDictionaryWords(dictionary.getId(), 0, pageSize, "filter");
        Mockito.verify(wordRepository, Mockito.times(1))
                .getDictionaryWordsStartsWithIgnoreCase(dictionary.getId(), pageable, "filter");
    }

    @Test
    void getAllPage0_shouldReturnWordsOnPage0WithCorrectSelectedFlag() {
        List<Word> wordsList = Arrays.asList(words);
        wordsList.sort(Comparator.comparing(Word::getWord));
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word");
        Mockito.when(wordRepository.findAllByWordStartsWithIgnoreCase(pageable, "filter"))
                .thenReturn(new PageImpl<Word>(
                        wordsList.subList(0, properties.getMaxPageSize()),
                        pageable, words.length)
                );
        List<WordDtoResponse> expectedList = wordsList
                .stream()
                .limit(properties.getMaxPageSize())
                .map(w -> new WordDtoResponse(
                        w.getId(),
                        w.getWord(),
                        w.getTranslation(),
                        true))
                .collect(Collectors.toList());
        WordsDtoResponse response =
                wordService.getAll(0, properties.getMaxPageSize(), "filter");
        Assertions.assertEquals(expectedList, response.getWords());
    }

    @Test
    void whenPageSizeTooBig_getAll_shouldSetMaxPageSize() {
        int pageSize = properties.getMaxPageSize() + 1;
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word");
        Mockito.when(wordRepository.findAllByWordStartsWithIgnoreCase(pageable, "filter"))
                .thenReturn(new PageImpl<Word>(Arrays.asList(words), pageable, words.length)
                );
        wordService.getAll(0, pageSize, "filter");
        Mockito.verify(wordRepository, Mockito.times(1))
                .findAllByWordStartsWithIgnoreCase(pageable, "filter");
    }

    @Test
    void whenPageSizeIsNegative_getAll_shouldSetMaxPageSize() {
        int pageSize = -1;
        Pageable pageable = new OffsetBasedPageable(0, properties.getMaxPageSize(),
                Sort.Direction.ASC, "word");
        Mockito.when(wordRepository.findAllByWordStartsWithIgnoreCase(pageable, "filter"))
                .thenReturn(
                        new PageImpl<Word>(Arrays.asList(words), pageable, words.length)
                );
        wordService.getAll(0, pageSize, "filter");
        Mockito.verify(wordRepository, Mockito.times(1))
                .findAllByWordStartsWithIgnoreCase(pageable, "filter");
    }


}
