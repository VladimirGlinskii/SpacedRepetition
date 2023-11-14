package net.thumbtack.spaced.repetition.service;

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
import net.thumbtack.spaced.repetition.utils.OffsetBasedPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service(value = "dictionaryService")
public class DictionaryService {
    private DictionaryRepository dictionaryRepository;

    private WordRepository wordRepository;

    private ApplicationProperties properties;

    @Autowired
    public DictionaryService(DictionaryRepository dictionaryRepository, WordRepository wordRepository, ApplicationProperties properties) {
        this.dictionaryRepository = dictionaryRepository;
        this.wordRepository = wordRepository;
        this.properties = properties;
    }

    public DictionariesDtoResponse getDictionaries(int page, int pageSize, String filter) {
        if (pageSize > properties.getMaxPageSize() || pageSize <= 0) {
            pageSize = properties.getMaxPageSize();
        }
        Page<Dictionary> dictionaries = dictionaryRepository.findAllByNameStartsWithIgnoreCase(
                new OffsetBasedPageable(
                        ((long) page) * pageSize,
                        pageSize,
                        Sort.Direction.ASC, "name"), filter);
        return new DictionariesDtoResponse(
                dictionaries.stream()
                        .map(d -> new DictionaryDtoResponse(d.getId(), d.getName()))
                        .collect(Collectors.toList()), dictionaries.getTotalPages()
        );
    }

    public DictionaryDtoResponse addDictionary(AddDictionaryRequest request) {
        if (dictionaryRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new ServerRuntimeException(ErrorCode.DICTIONARY_ALREADY_EXISTS);
        }
        Dictionary dictionary = new Dictionary(request.getName());
        dictionaryRepository.save(dictionary);
        return new DictionaryDtoResponse(dictionary.getId(), dictionary.getName());
    }

    public void addWord(int dictionaryId, int wordId) {
        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND));
        dictionary.getWords().add(word);
        dictionaryRepository.save(dictionary);
    }

    public void deleteWord(int dictionaryId, int wordId) {
        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND));
        dictionary.getWords().remove(word);
        dictionaryRepository.save(dictionary);
    }

    public void deleteDictionary(int dictionaryId) {
        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        dictionaryRepository.delete(dictionary);
    }

    public void renameDictionary(int dictionaryId, RenameDictionaryRequest request) {
        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        if (dictionaryRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new ServerRuntimeException(ErrorCode.DICTIONARY_ALREADY_EXISTS);
        }
        dictionary.setName(request.getName());
        dictionaryRepository.save(dictionary);
    }
}
