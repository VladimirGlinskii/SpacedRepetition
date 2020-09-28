package net.thumbtack.spaced.repetition.mock.service;

import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.request.dictionary.AddDictionaryRequest;
import net.thumbtack.spaced.repetition.dto.request.dictionary.RenameDictionaryRequest;
import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionariesDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionaryDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.model.Dictionary;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.repos.DictionaryRepository;
import net.thumbtack.spaced.repetition.repos.WordRepository;
import net.thumbtack.spaced.repetition.service.DictionaryService;
import net.thumbtack.spaced.repetition.utils.OffsetBasedPageable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DictionaryServiceMockTest extends ServiceMockTestBase {

    @Spy
    private final ApplicationProperties properties = getProperties();
    @InjectMocks
    private DictionaryService dictionaryService;
    @Mock
    private DictionaryRepository dictionaryRepository;
    @Mock
    private WordRepository wordRepository;

    @BeforeEach
    public void init() {
        super.init();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenPage0_getDictionaries_shouldReturnCorrectResponse() {
        List<Dictionary> dictionaries = Arrays.asList(
                new Dictionary(1, "name1", null),
                new Dictionary(2, "name2", null),
                new Dictionary(3, "name3", null)
        );
        int pageSize = 2;
        Pageable pageable = new OffsetBasedPageable(0, pageSize,
                Sort.Direction.ASC, "name");
        Mockito.when(dictionaryRepository.findAllByNameStartsWithIgnoreCase(pageable, "name")).thenReturn(
                new PageImpl<>(dictionaries.subList(0, pageSize), pageable, dictionaries.size())
        );
        DictionariesDtoResponse response = dictionaryService.getDictionaries(0, pageSize, "name");
        Assertions.assertTrue(
                dictionaries.stream().limit(pageSize)
                        .map(d -> new DictionaryDtoResponse(d.getId(), d.getName()))
                        .collect(Collectors.toList()).containsAll(response.getDictionaries())
        );
    }

    @Test
    void whenPageSizeTooBig_getDictionaries_shouldSetMaxPageSize() {
        int maxPageSize = properties.getMaxPageSize();
        int pageSize = maxPageSize + 1;
        List<Dictionary> dictionaries = Arrays.asList(
                new Dictionary(1, "name1", null),
                new Dictionary(2, "name2", null),
                new Dictionary(3, "name3", null)
        );
        Pageable pageable = new OffsetBasedPageable(0, maxPageSize,
                Sort.Direction.ASC, "name");
        Mockito.when(dictionaryRepository.findAllByNameStartsWithIgnoreCase(pageable, "")).thenReturn(
                new PageImpl<>(dictionaries,
                        pageable, dictionaries.size())
        );
        dictionaryService.getDictionaries(0, pageSize, "");
        Mockito.verify(dictionaryRepository, Mockito.times(1))
                .findAllByNameStartsWithIgnoreCase(pageable, "");
    }

    @Test
    void whenPageSizeIsNegative_getDictionaries_shouldSetMaxPageSize() {
        int maxPageSize = properties.getMaxPageSize();
        int pageSize = -1;
        List<Dictionary> dictionaries = Arrays.asList(
                new Dictionary(1, "name1", null),
                new Dictionary(2, "name2", null),
                new Dictionary(3, "name3", null)
        );
        Pageable pageable = new OffsetBasedPageable(0, maxPageSize,
                Sort.Direction.ASC, "name");
        Mockito.when(dictionaryRepository.findAllByNameStartsWithIgnoreCase(pageable, "")).thenReturn(
                new PageImpl<>(dictionaries,
                        pageable, dictionaries.size())
        );
        dictionaryService.getDictionaries(0, pageSize, "");
        Mockito.verify(dictionaryRepository, Mockito.times(1))
                .findAllByNameStartsWithIgnoreCase(pageable, "");
    }

    @Test
    void whenDictionaryNotExists_addDictionary_shouldSaveDictionaryAndReturnCorrectResponse() {
        AddDictionaryRequest request = new AddDictionaryRequest("name");
        DictionaryDtoResponse response = dictionaryService.addDictionary(request);
        ArgumentCaptor<Dictionary> dictionary = ArgumentCaptor.forClass(Dictionary.class);
        Mockito.verify(dictionaryRepository, Mockito.times(1))
                .save(dictionary.capture());
        Assertions.assertEquals(request.getName(), dictionary.getValue().getName());
        Assertions.assertEquals(request.getName(), response.getName());
    }

    @Test
    void whenDictionaryExists_addDictionary_shouldThrowException() {
        Mockito.when(dictionaryRepository.findByNameIgnoreCase("name"))
                .thenReturn(Optional.of(new Dictionary()));
        AddDictionaryRequest request = new AddDictionaryRequest("name");
        try {
            dictionaryService.addDictionary(request);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.DICTIONARY_ALREADY_EXISTS, e.getErrorCode());
        }
        Mockito.verify(dictionaryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void whenWordNotExists_addWord_shouldSaveWord() {
        Dictionary dictionary = new Dictionary(1, "name", new HashSet<>());
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.of(words[0]));
        dictionaryService.addWord(1, words[0].getId());
        ArgumentCaptor<Dictionary> savedDictionary = ArgumentCaptor.forClass(Dictionary.class);
        Mockito.verify(dictionaryRepository, Mockito.times(1))
                .save(savedDictionary.capture());
        Assertions.assertEquals(1, savedDictionary.getValue().getWords().size());
        Assertions.assertEquals(dictionary.getId(), savedDictionary.getValue().getId());
        Assertions.assertEquals(dictionary.getName(), savedDictionary.getValue().getName());
        Assertions.assertTrue(savedDictionary.getValue().getWords().contains(words[0]));
    }

    @Test
    void whenDictionaryNotExists_addWord_shouldThrowException() {
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.empty());
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.of(words[0]));
        try {
            dictionaryService.addWord(1, words[0].getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, e.getErrorCode());
        }
    }

    @Test
    void whenWordNotExists_addWord_shouldThrowException() {
        Dictionary dictionary = new Dictionary(1, "name", new HashSet<>());
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.empty());
        try {
            dictionaryService.addWord(1, words[0].getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WORD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    void deleteWord_shouldDeleteWord() {
        Dictionary dictionary = new Dictionary(1, "name",
                new HashSet<Word>() {{
                    add(words[0]);
                }});
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.of(words[0]));
        dictionaryService.deleteWord(1, words[0].getId());
        ArgumentCaptor<Dictionary> savedDictionary = ArgumentCaptor.forClass(Dictionary.class);
        Mockito.verify(dictionaryRepository, Mockito.times(1))
                .save(savedDictionary.capture());
        Assertions.assertEquals(0, savedDictionary.getValue().getWords().size());
        Assertions.assertEquals(dictionary.getId(), savedDictionary.getValue().getId());
        Assertions.assertEquals(dictionary.getName(), savedDictionary.getValue().getName());
    }

    @Test
    void whenDictionaryNotExists_deleteWord_shouldThrowException() {
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.empty());
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.of(words[0]));
        try {
            dictionaryService.deleteWord(1, words[0].getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, e.getErrorCode());
        }
    }

    @Test
    void whenWordNotExists_deleteWord_shouldThrowException() {
        Dictionary dictionary = new Dictionary(1, "name", new HashSet<>());
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.empty());
        try {
            dictionaryService.deleteWord(1, words[0].getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WORD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    void whenDictionaryNotExists_deleteDictionary_shouldThrowException() {
        Mockito.when(dictionaryRepository.findById(1))
                .thenThrow(new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        try {
            dictionaryService.deleteDictionary(1);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, e.getErrorCode());
        }
    }

    @Test
    void whenDictionaryExists_deleteDictionary_shouldDeleteDictionary() {
        Dictionary dictionary = new Dictionary(1, "name", new HashSet<>());
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        dictionaryService.deleteDictionary(1);
        Mockito.verify(dictionaryRepository, Mockito.times(1))
                .delete(dictionary);
    }

    @Test
    void whenDictionaryNotExists_renameDictionary_shouldThrowException() {
        Dictionary dictionary = new Dictionary(1, "name", new HashSet<>());
        Mockito.when(dictionaryRepository.findById(1))
                .thenReturn(Optional.of(dictionary));
        RenameDictionaryRequest request = new RenameDictionaryRequest("newName");
        Mockito.when(dictionaryRepository.findByNameIgnoreCase("newName"))
                .thenThrow(new ServerRuntimeException(ErrorCode.DICTIONARY_ALREADY_EXISTS));
        try {
            dictionaryService.renameDictionary(dictionary.getId(), request);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.DICTIONARY_ALREADY_EXISTS, e.getErrorCode());
        }
    }

    @Test
    void whenDictionaryNameAlreadyExists_renameDictionary_shouldThrowException() {
        RenameDictionaryRequest request = new RenameDictionaryRequest("newName");
        Mockito.when(dictionaryRepository.findById(1))
                .thenThrow(new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        try {
            dictionaryService.renameDictionary(1, request);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, e.getErrorCode());
        }
    }

    @Test
    void whenWordExists_editWord_shouldUpdateWord() {
        Dictionary dictionary = new Dictionary(1, "name", new HashSet<>());
        Mockito.when(dictionaryRepository.findById(dictionary.getId()))
                .thenReturn(Optional.of(dictionary));
        String newName = "newName";
        RenameDictionaryRequest request = new RenameDictionaryRequest(newName);
        dictionaryService.renameDictionary(1, request);

        ArgumentCaptor<Dictionary> savedDictionary = ArgumentCaptor.forClass(Dictionary.class);
        Mockito.verify(dictionaryRepository, Mockito.times(dictionary.getId()))
                .save(savedDictionary.capture());
        Assertions.assertEquals(newName, savedDictionary.getValue().getName());
    }
}
