package net.thumbtack.spaced.repetition.integrational;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.commons.AdminControllerUtils;
import net.thumbtack.spaced.repetition.commons.DictionaryControllerUtils;
import net.thumbtack.spaced.repetition.commons.ExerciseControllerUtils;
import net.thumbtack.spaced.repetition.commons.UserControllerUtils;
import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.enums.AnswerStatus;
import net.thumbtack.spaced.repetition.dto.request.dictionary.AddDictionaryRequest;
import net.thumbtack.spaced.repetition.dto.request.exercise.CheckAnswerDtoRequest;
import net.thumbtack.spaced.repetition.dto.request.user.LoginDtoRequest;
import net.thumbtack.spaced.repetition.dto.request.user.RegisterDtoRequest;
import net.thumbtack.spaced.repetition.dto.request.word.AddWordRequest;
import net.thumbtack.spaced.repetition.dto.response.ErrorsResponse;
import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionariesDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionaryDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.CheckAnswerDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.ExerciseDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.LoginDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.RegisterDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.UserDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.UsersDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.word.AddWordResponse;
import net.thumbtack.spaced.repetition.dto.response.word.GetDictionaryWordsDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.word.WordDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.model.Dictionary;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.repos.*;
import net.thumbtack.spaced.repetition.utils.OffsetBasedPageable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@SpringBootTest
public class IntegrationTest {
    private RegisterDtoRequest standardUser = new RegisterDtoRequest(
                    "Username",
                    "username@gmail.com",
                    "password"
            );

    private User admin;

    private List<Word> words;

    private List<Dictionary> dictionaries;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersWordRepository usersWordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationProperties properties;

    @BeforeEach
    public void init() {
        if (admin == null) {
            admin = userRepository.findByUsernameIgnoreCase("admin");
            admin.setPassword("admin");
            Pageable pageable = new OffsetBasedPageable(
                    0,
                    properties.getMaxPageSize() * 2,
                    Sort.Direction.ASC,
                    "id");
            words = wordRepository.findAll(pageable).getContent();
            dictionaries = dictionaryRepository.findAll(pageable).getContent();
        }
    }

    private <T> T getResponse(MvcResult mvcResult, Class<T> clazz) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
        } catch (Exception e) {
            return null;
        }
    }

    private <T> String toJson(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "";
        }
    }

    private String getSimpleTranslation(Word word) {
        return word.getTranslation().replaceAll("[\\[\\]\"]", "");
    }

    private Set<String> getTranslationAsSet(Word word) {
        try {
            return objectMapper.readValue(word.getTranslation(), new TypeReference<Set<String>>() {
            });
        } catch (JsonProcessingException e) {
            return new HashSet<>();
        }
    }

    private int register(String username, String email, String password) throws Exception {
        return Objects.requireNonNull(getResponse(
                UserControllerUtils.register(mockMvc,
                        toJson(new RegisterDtoRequest(username, email, password))),
                RegisterDtoResponse.class))
                .getId();
    }

    private String login(String username, String password) throws Exception {
        return Objects.requireNonNull(getResponse(
                UserControllerUtils.login(mockMvc,
                        toJson(new LoginDtoRequest(username, password))),
                LoginDtoResponse.class
        )).getToken();
    }

    @Test
    public void registerAndLogin_shouldReturnOkStatus() throws Exception {
        MvcResult registerRes = UserControllerUtils.register(
                mockMvc,
                toJson(standardUser)
        );
        Assertions.assertEquals(HttpStatus.OK.value(), registerRes.getResponse().getStatus());
        MvcResult loginRes = UserControllerUtils.login(
                mockMvc,
                toJson(new LoginDtoRequest(standardUser.getUsername(), standardUser.getPassword()))
        );
        Assertions.assertEquals(HttpStatus.OK.value(), loginRes.getResponse().getStatus());
    }

    @Test
    public void whenAdmin_addDictionary_addWords_addWordsToDictionary() throws Exception {
        String adminToken = login(admin.getUsername(), admin.getPassword());

        MvcResult addDictionary = AdminControllerUtils
                .addDictionary(mockMvc, adminToken, toJson(new AddDictionaryRequest("someName")));
        DictionaryDtoResponse dictionary = getResponse(addDictionary, DictionaryDtoResponse.class);
        Assertions.assertEquals(
                HttpStatus.OK.value(), addDictionary.getResponse().getStatus());

        MvcResult addWord1 = AdminControllerUtils
                .addWord(mockMvc, adminToken, toJson(new AddWordRequest("word1", "transl1")));
        AddWordResponse word1 = getResponse(addWord1, AddWordResponse.class);
        Assertions.assertEquals(
                HttpStatus.OK.value(), addWord1.getResponse().getStatus());

        MvcResult addWord2 = AdminControllerUtils
                .addWord(mockMvc, adminToken, toJson(new AddWordRequest("word2", "transl2, transl3")));
        AddWordResponse word2 = getResponse(addWord2, AddWordResponse.class);
        Assertions.assertEquals(
                HttpStatus.OK.value(), addWord2.getResponse().getStatus());

        Assertions.assertEquals(
                HttpStatus.OK.value(),
                AdminControllerUtils
                        .addWordToDictionary(mockMvc, adminToken, dictionary.getId(), word1.getId())
                        .getResponse().getStatus()
        );

        Assertions.assertEquals(
                HttpStatus.OK.value(),
                AdminControllerUtils
                        .addWordToDictionary(mockMvc, adminToken, dictionary.getId(), word2.getId())
                        .getResponse().getStatus()
        );

        Set<WordDtoResponse> wordsResponse = getResponse(
                DictionaryControllerUtils.getDictionaryWordsForUser(
                        mockMvc, adminToken, dictionary.getId(), 0, properties.getMaxPageSize(), ""),
                GetDictionaryWordsDtoResponse.class
        ).getWords();

        Assertions.assertEquals(2, wordsResponse.size());
    }

    @Test
    public void whenLogged_testExercisingProcess() throws Exception {
        register(standardUser.getUsername(), standardUser.getEmail(), standardUser.getPassword());
        String token = login(standardUser.getUsername(), standardUser.getPassword());
        List<DictionaryDtoResponse> dictionaries = Objects.requireNonNull(getResponse(
                DictionaryControllerUtils.getDictionaries(mockMvc, token, 0, properties.getMaxPageSize(), "food"),
                DictionariesDtoResponse.class)).getDictionaries();

        Set<WordDtoResponse> wordsResponse = getResponse(
                DictionaryControllerUtils.getDictionaryWordsForUser(
                        mockMvc, token, dictionaries.get(0).getId(), 0, properties.getMaxPageSize(), ""),
                GetDictionaryWordsDtoResponse.class
        ).getWords();

        Assertions.assertTrue(wordsResponse.size() > 0);

        for (WordDtoResponse w : wordsResponse) {
            Assertions.assertEquals(
                    HttpStatus.OK.value(),
                    UserControllerUtils.addUsersWord(mockMvc, token, w.getId())
                            .getResponse().getStatus());
        }

        ExerciseDtoResponse exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        CheckAnswerDtoResponse wrongAnswerResponse = getResponse(
                ExerciseControllerUtils.checkAnswer(mockMvc, token,
                        toJson(new CheckAnswerDtoRequest(exercise.getUuid(), "wrong"))),
                CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.INCORRECT, wrongAnswerResponse.getStatus());

        exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        Set<String> correctTranslations = getTranslationAsSet(wordRepository.findByWordIgnoreCase(exercise.getWord()).get());
        CheckAnswerDtoResponse correctAnswerResponse = getResponse(
                ExerciseControllerUtils.checkAnswer(mockMvc, token,
                        toJson(new CheckAnswerDtoRequest(exercise.getUuid(), correctTranslations.iterator().next()))),
                CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.CORRECT, correctAnswerResponse.getStatus());

        for (WordDtoResponse w : wordsResponse) {
            Assertions.assertEquals(
                    HttpStatus.OK.value(),
                    UserControllerUtils.deleteUsersWord(mockMvc, token, w.getId())
                            .getResponse().getStatus());
        }

        exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        Assertions.assertNull(exercise.getWord());
        Assertions.assertNull(exercise.getUuid());
    }

    @Test
    public void testAnsweringOnExercises() throws Exception {
        int userId = register(standardUser.getUsername(), standardUser.getEmail(), standardUser.getPassword());
        String token = login(standardUser.getUsername(), standardUser.getPassword());

        String[] correctTranslations = {"transl1", "transl2", "transl3"};

        Word word = wordRepository.save(new Word("word",
                toJson(new HashSet<String>() {{
                    add(correctTranslations[0]);
                    add(correctTranslations[1]);
                    add(correctTranslations[2]);
                }})));

        UserControllerUtils.addUsersWord(mockMvc, token, word.getId());

        ExerciseDtoResponse exercise;
        CheckAnswerDtoResponse answerResponse;

        exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        answerResponse = getResponse(
                ExerciseControllerUtils.checkAnswer(mockMvc, token,
                        toJson(new CheckAnswerDtoRequest(exercise.getUuid(), "wrong"))),
                CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.INCORRECT, answerResponse.getStatus());

        exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        answerResponse = getResponse(
                ExerciseControllerUtils.checkAnswer(mockMvc, token,
                        toJson(new CheckAnswerDtoRequest(exercise.getUuid(), correctTranslations[0]))),
                CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.CORRECT, answerResponse.getStatus());

        exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        answerResponse = getResponse(
                ExerciseControllerUtils.checkAnswer(mockMvc, token,
                        toJson(new CheckAnswerDtoRequest(exercise.getUuid(), correctTranslations[1]))),
                CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.CORRECT, answerResponse.getStatus());

        exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        answerResponse = getResponse(
                ExerciseControllerUtils.checkAnswer(mockMvc, token,
                        toJson(new CheckAnswerDtoRequest(exercise.getUuid(), correctTranslations[2]))),
                CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.CORRECT, answerResponse.getStatus());

        UsersWord usersWord = usersWordRepository.findByUserIdAndWordId(userId, word.getId());

        Assertions.assertEquals(3, usersWord.getLastInterval());

        exercise = getResponse(ExerciseControllerUtils.getExercise(mockMvc, token), ExerciseDtoResponse.class);
        answerResponse = getResponse(
                ExerciseControllerUtils.checkAnswer(mockMvc, token,
                        toJson(new CheckAnswerDtoRequest(exercise.getUuid(), "wrong"))),
                CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.INCORRECT, answerResponse.getStatus());

        usersWord = usersWordRepository.findByUserIdAndWordId(userId, word.getId());

        Assertions.assertEquals(2, usersWord.getLastInterval());
    }

    @Test
    public void testGetDictionariesFiltering() throws Exception {
        String token = login(admin.getUsername(), admin.getPassword());

        AdminControllerUtils.addDictionary(mockMvc, token, toJson(new AddDictionaryRequest("[test]name1")));

        AdminControllerUtils.addDictionary(mockMvc, token, toJson(new AddDictionaryRequest("[test]name12")));

        List<DictionaryDtoResponse> dictionaries = Objects.requireNonNull(getResponse(
                DictionaryControllerUtils.getDictionaries(mockMvc, token, 0, 3, "[test]name1"),
                DictionariesDtoResponse.class)).getDictionaries();

        Assertions.assertEquals(2, dictionaries.size());

        dictionaries = Objects.requireNonNull(getResponse(
                DictionaryControllerUtils.getDictionaries(mockMvc, token, 0, 3, "[test]name12"),
                DictionariesDtoResponse.class)).getDictionaries();

        Assertions.assertEquals(1, dictionaries.size());

        dictionaries = Objects.requireNonNull(getResponse(
                DictionaryControllerUtils.getDictionaries(mockMvc, token, 0, 3, null),
                DictionariesDtoResponse.class)).getDictionaries();

        Assertions.assertEquals(3, dictionaries.size());

    }

    @Test
    public void testGetUsersFiltering() throws Exception {
        String token = login(admin.getUsername(), admin.getPassword());

        register("testuser1", "testuser1@gmail.com", "password");

        register("testuser12", "testuser12@gmail.com", "password");

        List<UserDtoResponse> users = Objects.requireNonNull(getResponse(
                AdminControllerUtils.getAllUsers(mockMvc, token, 0, 3, "testuser1"),
                UsersDtoResponse.class)).getUsers();

        Assertions.assertEquals(2, users.size());

        users = Objects.requireNonNull(getResponse(
                AdminControllerUtils.getAllUsers(mockMvc, token, 0, 3, "testuser12"),
                UsersDtoResponse.class)).getUsers();

        Assertions.assertEquals(1, users.size());

        users = Objects.requireNonNull(getResponse(
                AdminControllerUtils.getAllUsers(mockMvc, token, 0, 3, null),
                UsersDtoResponse.class)).getUsers();

        Assertions.assertEquals(3, users.size());

    }

    @Test
    public void testGetDictionaryWordsForUserFiltering() throws Exception {
        String token = login(admin.getUsername(), admin.getPassword());

        int pageSize = 3;

        DictionaryDtoResponse dictionary = Objects.requireNonNull(getResponse(
                DictionaryControllerUtils.getDictionaries(mockMvc, token, 0, pageSize, "food"),
                DictionariesDtoResponse.class)).getDictionaries().get(0);

        AdminControllerUtils.addWord(mockMvc, token,
                toJson(new AddWordRequest("[test]word1", "transl")), dictionary.getId());

        AdminControllerUtils.addWord(mockMvc, token,
                toJson(new AddWordRequest("[test]word12", "transl")), dictionary.getId());

        AdminControllerUtils.addWord(mockMvc, token,
                toJson(new AddWordRequest("[test]word123", "transl")));

        Set<WordDtoResponse> words = getResponse(DictionaryControllerUtils
                        .getDictionaryWordsForUser(mockMvc, token, dictionary.getId(),
                                0, pageSize, "[test]word1"),
                GetDictionaryWordsDtoResponse.class).getWords();

        Assertions.assertEquals(2, words.size());

        words = getResponse(DictionaryControllerUtils.getDictionaryWordsForUser(mockMvc, token, dictionary.getId(),
                0, pageSize, "[test]word12"),
                GetDictionaryWordsDtoResponse.class).getWords();

        Assertions.assertEquals(1, words.size());

        words = getResponse(DictionaryControllerUtils.getDictionaryWordsForUser(mockMvc, token, dictionary.getId(),
                0, pageSize, ""),
                GetDictionaryWordsDtoResponse.class).getWords();

        Assertions.assertEquals(3, words.size());

        words = getResponse(DictionaryControllerUtils.getDictionaryWordsForUser(mockMvc, token, 0,
                0, pageSize, "[test]word12"),
                GetDictionaryWordsDtoResponse.class).getWords();

        Assertions.assertEquals(2, words.size());
    }

    @Test
    public void whenAdmin_uploadWordsFromFile_shouldAddWordsAndReturnOkStatus() throws Exception {
        String token = login(admin.getUsername(), admin.getPassword());

        AdminControllerUtils.addDictionary(mockMvc, token,
                toJson(new AddDictionaryRequest("dictionary")));
        Dictionary dictionary = dictionaryRepository.findByNameIgnoreCase("dictionary").get();

        String fileData = "test1,пер1,пер2\n" +
                "test2,пер3,\n" +
                ",,\n" +
                "test3,пер4,пер5";

        MvcResult result = AdminControllerUtils
                .uploadWordsFromFile(mockMvc, token, fileData, dictionary.getId());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        dictionary = dictionaryRepository.findByNameIgnoreCase("dictionary").get();

        Assertions.assertEquals(3, dictionary.getWords().size());

        Assertions.assertTrue(
                new HashSet<>(Arrays.asList("пер1", "пер2")).containsAll(
                        getTranslationAsSet(wordRepository.findByWordIgnoreCase("test1").get())));

        Assertions.assertTrue(
                new HashSet<>(Collections.singletonList("пер3")).containsAll(
                        getTranslationAsSet(wordRepository.findByWordIgnoreCase("test2").get())));

        Assertions.assertTrue(
                new HashSet<>(Arrays.asList("пер4", "пер5")).containsAll(
                        getTranslationAsSet(wordRepository.findByWordIgnoreCase("test3").get())));

    }

    @Test
    public void whenFileIsEmpty_uploadWordsFromFile_shouldReturnBadRequest() throws Exception {
        String token = login(admin.getUsername(), admin.getPassword());

        AdminControllerUtils.addDictionary(mockMvc, token,
                toJson(new AddDictionaryRequest("dictionary")));
        Dictionary dictionary = dictionaryRepository.findByNameIgnoreCase("dictionary").get();

        String fileData = "";

        MvcResult result = AdminControllerUtils
                .uploadWordsFromFile(mockMvc, token, fileData, dictionary.getId());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorsResponse response = getResponse(result, ErrorsResponse.class);

        Assertions.assertEquals(ErrorCode.FILE_IS_EMPTY, response.getErrors().get(0).getErrorCode());
    }

    @Test
    public void whenDictionaryNotExists_uploadWordsFromFile_shouldReturnBadRequest() throws Exception {
        String token = login(admin.getUsername(), admin.getPassword());

        AdminControllerUtils.addDictionary(mockMvc, token,
                toJson(new AddDictionaryRequest("dictionary")));
        Dictionary dictionary = dictionaryRepository.findByNameIgnoreCase("dictionary").get();

        String fileData = "word, перевод";

        MvcResult result = AdminControllerUtils
                .uploadWordsFromFile(mockMvc, token, fileData, -1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorsResponse response = getResponse(result, ErrorsResponse.class);

        Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, response.getErrors().get(0).getErrorCode());
    }

    @Test
    public void whenWordWithoutTranslation_uploadWordsFromFile_shouldReturnBadRequest() throws Exception {
        String token = login(admin.getUsername(), admin.getPassword());

        AdminControllerUtils.addDictionary(mockMvc, token,
                toJson(new AddDictionaryRequest("dictionary")));
        Dictionary dictionary = dictionaryRepository.findByNameIgnoreCase("dictionary").get();

        String fileData = "test1,пер1,пер2\n" +
                "test2,,\n" +
                ",,\n" +
                "test3,пер4,пер5";

        MvcResult result = AdminControllerUtils
                .uploadWordsFromFile(mockMvc, token, fileData, dictionary.getId());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorsResponse response = getResponse(result, ErrorsResponse.class);

        Assertions.assertEquals(ErrorCode.NO_TRANSLATIONS_PRESENTED, response.getErrors().get(0).getErrorCode());
    }

}
