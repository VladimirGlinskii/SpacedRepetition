package net.thumbtack.spaced.repetition.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.request.word.AddWordRequest;
import net.thumbtack.spaced.repetition.dto.request.word.EditWordRequest;
import net.thumbtack.spaced.repetition.dto.response.word.*;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.model.Dictionary;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import net.thumbtack.spaced.repetition.repos.DictionaryRepository;
import net.thumbtack.spaced.repetition.repos.UsersWordRepository;
import net.thumbtack.spaced.repetition.repos.WordRepository;
import net.thumbtack.spaced.repetition.utils.OffsetBasedPageable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static net.thumbtack.spaced.repetition.utils.ApplicationConstants.*;

@Service(value = "wordService")
public class WordService {
    private final UsersWordRepository usersWordRepository;
    private final WordRepository wordRepository;
    private final DictionaryRepository dictionaryRepository;
    private final ApplicationProperties properties;
    private final ObjectMapper objectMapper;

    @Autowired
    public WordService(UsersWordRepository usersWordRepository,
                       WordRepository wordRepository,
                       DictionaryRepository dictionaryRepository,
                       ObjectMapper objectMapper,
                       ApplicationProperties properties) {
        this.usersWordRepository = usersWordRepository;
        this.wordRepository = wordRepository;
        this.dictionaryRepository = dictionaryRepository;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public GetDictionaryWordsDtoResponse getDictionaryWordsForUser(int id, int page, int pageSize, String filter) {
        if (pageSize > properties.getMaxPageSize() || pageSize <= 0) {
            pageSize = properties.getMaxPageSize();
        }
        User user = ServiceUtils.getUser();
        Page<UsersWord> usersWords;
        Set<WordDtoResponse> words;
        Pageable pageable = new OffsetBasedPageable(
                ((long) page) * pageSize,
                pageSize,
                Sort.Direction.ASC, "word.word");
        if (id == 0) {
            usersWords = usersWordRepository.getAllWordsWithUserStatusStartsWithIgnoreCase(
                    user.getId(), pageable, filter);
        } else {
            usersWords = usersWordRepository.getDictionaryWordsWithUserStatusStartsWithIgnoreCase(
                    id, user.getId(), pageable, filter);
        }
        words = usersWords.stream()
                .map(w -> new WordDtoResponse(w.getWord().getId(), w.getWord().getWord(),
                        w.getWord().getTranslation(),
                        UsersWordStatus.ACTIVE == w.getStatus()))
                .collect(Collectors.toSet());

        return new GetDictionaryWordsDtoResponse(words, usersWords.getTotalPages());
    }

    @Transactional
    public AddWordResponse addWord(AddWordRequest request, int dictionaryId) {
        Optional<Word> wordOptional = wordRepository.findByWordIgnoreCase(request.getWord());
        String[] requestTranslations = request.getTranslation().split(",\\s*");
        Word word = wordOptional.orElse(new Word(request.getWord(), "[]"));
        Set<String> translations;
        try {
            translations = objectMapper.readValue(word.getTranslation(), SET_TYPE);
            translations.addAll(Arrays.asList(requestTranslations));
            word.setTranslation(objectMapper.writeValueAsString(translations));
        } catch (JsonProcessingException e) {
            throw new ServerRuntimeException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
        wordRepository.save(word);
        if (dictionaryId != 0) {
            Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                    .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
            dictionary.getWords().add(word);
            dictionaryRepository.save(dictionary);
        }
        return new AddWordResponse(word.getId(), word.getWord());
    }

    public void editWord(EditWordRequest request, int wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND));
        String[] requestTranslations = request.getTranslation().split("\\s*[,;]\\s*");
        word.setWord(request.getWord());
        try {
            word.setTranslation(objectMapper.writeValueAsString(new HashSet<>(Arrays.asList(requestTranslations))));
        } catch (JsonProcessingException e) {
            throw new ServerRuntimeException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
        wordRepository.save(word);
    }

    public void deleteWord(int wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND));
        wordRepository.delete(word);
    }

    public GetWordsForDictionaryResponse getWordsForDictionary(int dictionaryId, int page, int pageSize, String filter) {
        if (pageSize > properties.getMaxPageSize() || pageSize <= 0) {
            pageSize = properties.getMaxPageSize();
        }
        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        Page<Word> words = wordRepository.findAllByWordStartsWithIgnoreCase(
                new OffsetBasedPageable(
                        ((long) page) * pageSize,
                        pageSize,
                        Sort.Direction.ASC, "word"), filter);
        Set<Word> dictionaryWords = dictionary.getWords();
        List<WordDtoResponse> responseList =
                words.stream().map(
                        w -> new WordDtoResponse(w.getId(), w.getWord(),
                                w.getTranslation(), dictionaryWords.contains(w))
                ).collect(Collectors.toList());
        return new GetWordsForDictionaryResponse(responseList, words.getTotalPages());
    }

    public WordsDtoResponse getAll(int page, int pageSize, String filter) {
        if (pageSize > properties.getMaxPageSize() || pageSize <= 0) {
            pageSize = properties.getMaxPageSize();
        }
        Page<Word> words = wordRepository.findAllByWordStartsWithIgnoreCase(
                new OffsetBasedPageable(
                        ((long) page) * pageSize,
                        pageSize,
                        Sort.Direction.ASC, "word"), filter);
        List<WordDtoResponse> responseList =
                words.stream().map(
                        w -> new WordDtoResponse(w.getId(), w.getWord(),
                                w.getTranslation(), true)
                ).collect(Collectors.toList());
        return new WordsDtoResponse(responseList, words.getTotalPages());
    }

    public GetWordsForDictionaryResponse getDictionaryWords(int dictionaryId, int page,
                                                            int pageSize, String filter) {
        if (pageSize > properties.getMaxPageSize() || pageSize <= 0) {
            pageSize = properties.getMaxPageSize();
        }
        dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        Page<Word> words = wordRepository.getDictionaryWordsStartsWithIgnoreCase(
                dictionaryId,
                new OffsetBasedPageable(
                        ((long) page) * pageSize,
                        pageSize,
                        Sort.Direction.ASC, "word.word"), filter);
        List<WordDtoResponse> responseList =
                words.stream().map(
                        w -> new WordDtoResponse(w.getId(), w.getWord(),
                                w.getTranslation(), true)
                ).collect(Collectors.toList());
        return new GetWordsForDictionaryResponse(responseList, words.getTotalPages());

    }

    @Transactional
    public void uploadWordsFromFile(MultipartFile file, int dictionaryId) {
        if (!file.isEmpty()) {
            Dictionary dictionary = null;
            Set<Word> wordSet = new HashSet<>();
            if (dictionaryId != 0) {
                dictionary = dictionaryRepository.findById(dictionaryId)
                        .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                br.lines()
                        .map(l -> l.split("\\s*[,;]\\s*"))
                        .filter(l -> l.length > 0 && !"".equals(l[0]))
                        .forEach(line ->
                                {
                                    Set<String> translations = new HashSet<>();
                                    for (int i = 1; i < line.length; i++) {
                                        if (!"".equals(line[i])) {
                                            translations.add(line[i]);
                                        }
                                    }
                                    if (translations.size() == 0) {
                                        throw new ServerRuntimeException(ErrorCode.NO_TRANSLATIONS_PRESENTED);
                                    }
                                    Word word = wordRepository.findByWordIgnoreCase(line[0])
                                            .orElse(new Word(line[0], "[]"));
                                    try {
                                        translations.addAll(objectMapper.readValue(word.getTranslation(), SET_TYPE));
                                        word.setTranslation(objectMapper.writeValueAsString(translations));
                                    } catch (JsonProcessingException e) {
                                        throw new ServerRuntimeException(ErrorCode.UNKNOWN_SERVER_ERROR);
                                    }
                                    wordSet.add(word);
                                }
                        );
            } catch (IOException e) {
                throw new ServerRuntimeException(ErrorCode.COULD_NOT_READ_FILE);
            }

            wordRepository.saveAll(wordSet);

            if (dictionary != null) {
                dictionary.getWords().addAll(wordSet);
                dictionaryRepository.save(dictionary);
            }

        } else {
            throw new ServerRuntimeException(ErrorCode.FILE_IS_EMPTY);
        }

    }

    public PronunciationLinkResponse getWordPronunciationLink(String word) {
        if (word == null || word.length() == 0) {
            return new PronunciationLinkResponse(null);
        }
        try {
            Document doc = Jsoup
                    .connect(PRONUNCIATION_BASIC_URL + word).get();
            Elements urls = doc.getElementsByAttribute(MP3_ATTRIBUTE);
            String mp3 = urls.get(0).attr(MP3_ATTRIBUTE);
            return new PronunciationLinkResponse(mp3);
        } catch (Exception e) {
            throw new ServerRuntimeException(ErrorCode.CANT_FIND_PRONUNCIATION);
        }
    }
}
