package net.thumbtack.spaced.repetition.mock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.enums.AnswerStatus;
import net.thumbtack.spaced.repetition.dto.request.exercise.CheckAnswerDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.exercise.CheckAnswerDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.ExerciseDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.model.Exercise;
import net.thumbtack.spaced.repetition.model.Statistics;
import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.enums.ExerciseStatus;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import net.thumbtack.spaced.repetition.repos.ExerciseRepository;
import net.thumbtack.spaced.repetition.repos.StatisticsRepository;
import net.thumbtack.spaced.repetition.repos.UsersWordRepository;
import net.thumbtack.spaced.repetition.service.ExerciseService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExerciseServiceMockTest extends ServiceMockTestBase {

    @Spy
    private final ApplicationProperties properties = getProperties();
    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private ExerciseService exerciseService;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private StatisticsRepository statisticsRepository;
    @Mock
    private UsersWordRepository usersWordRepository;

    @BeforeEach
    public void init() {
        super.init();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenExerciseNotAnswered_getExercise_shouldReturnThatExercise() {
        mockSecurityContextHolderForUser(standardUser);
        Exercise exercise = new Exercise(standardUsersWords[0], standardUser);
        exercise.setUuid(UUID.randomUUID());
        Mockito.when(exerciseRepository
                .findTopByUserIdAndStatus(standardUser.getId(), ExerciseStatus.NO_ANSWER))
                .thenReturn(exercise);
        ExerciseDtoResponse response = exerciseService.getExercise();
        Mockito.verify(exerciseRepository, Mockito.never()).save(Mockito.any());
        Assertions.assertEquals(exercise.getUuid(), response.getUuid());
        Assertions.assertEquals(exercise.getUsersWord().getWord().getWord(), response.getWord());

    }

    @Test
    void whenAllExercisesAnswered_getExercise_shouldCreateNewExercise() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(exerciseRepository
                .findTopByUserIdAndStatus(standardUser.getId(), ExerciseStatus.NO_ANSWER))
                .thenReturn(null);
        Mockito.when(usersWordRepository
                .findTopByUserIdAndStatusOrderByNextDatetimeAsc(
                        standardUser.getId(), UsersWordStatus.ACTIVE))
                .thenReturn(standardUsersWords[0]);
        ExerciseDtoResponse response = exerciseService.getExercise();
        Assertions.assertEquals(standardUsersWords[0].getWord().getWord(), response.getWord());
        ArgumentCaptor<Exercise> exerciseCaptor = ArgumentCaptor.forClass(Exercise.class);
        Mockito.verify(exerciseRepository, Mockito.times(1))
                .save(exerciseCaptor.capture());
        Assertions.assertEquals(standardUsersWords[0], exerciseCaptor.getValue().getUsersWord());
    }

    @Test
    void whenUsersWordsEmpty_getExercise_shouldReturnResponseWithNulls() {
        mockSecurityContextHolderForUser(standardUser);
        ExerciseDtoResponse response = exerciseService.getExercise();
        Assertions.assertNull(response.getWord());
        Assertions.assertNull(response.getUuid());
        Mockito.verify(exerciseRepository, Mockito.times(1))
                .findTopByUserIdAndStatus(standardUser.getId(), ExerciseStatus.NO_ANSWER);
        Mockito.verify(usersWordRepository, Mockito.times(1))
                .findTopByUserIdAndStatusOrderByNextDatetimeAsc(
                        standardUser.getId(), UsersWordStatus.ACTIVE);
        Mockito.verify(exerciseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void whenAnswerIsCorrect_checkAnswer_shouldIncreaseIntervalAndReturnCorrectDto() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(statisticsRepository.findByUserIdAndDate(standardUser.getId(), LocalDate.now()))
                .thenReturn(Optional.empty());

        LocalDateTime now = LocalDateTime.now();
        UUID uuid = java.util.UUID.randomUUID();
        Exercise exercise = new Exercise(standardUsersWords[0], standardUser);
        exercise.setUuid(uuid);
        Mockito.when(exerciseRepository.findById(uuid))
                .thenReturn(Optional.of(exercise));
        ArgumentCaptor<Exercise> savedExercise = ArgumentCaptor.forClass(Exercise.class);
        CheckAnswerDtoResponse response = exerciseService.checkAnswer(
                new CheckAnswerDtoRequest(uuid, getSimpleTranslation(standardUsersWords[0].getWord()))
        );
        Assertions.assertEquals(AnswerStatus.CORRECT, response.getStatus());
        Mockito.verify(exerciseRepository, Mockito.times(1))
                .save(savedExercise.capture());
        Assertions.assertEquals(ExerciseStatus.RIGHT_ANSWER, savedExercise.getValue().getStatus());
        Assertions.assertNotNull(savedExercise.getValue().getAnswerDatetime());
        Assertions.assertEquals(uuid, savedExercise.getValue().getUuid());
        Assertions.assertEquals(standardUser, savedExercise.getValue().getUser());

        UsersWord savedUsersWord = savedExercise.getValue().getUsersWord();
        Assertions.assertEquals(standardUsersWords[0].getId(), savedUsersWord.getId());
        Assertions.assertEquals(1, savedUsersWord.getLastInterval());
        Assertions.assertTrue(savedUsersWord.getNextDatetime()
                .isAfter(now));

        Mockito.verify(statisticsRepository, Mockito.times(1))
                .save(new Statistics(0, standardUser, 1, 0, LocalDate.now()));
    }

    @Test
    void whenAnswerIsWrong_checkAnswer_shouldDecreaseIntervalAndReturnCorrectDto() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(statisticsRepository.findByUserIdAndDate(standardUser.getId(), LocalDate.now()))
                .thenReturn(Optional.empty());

        UUID uuid = java.util.UUID.randomUUID();
        standardUsersWords[0].setLastInterval(1);
        Exercise exercise = new Exercise(standardUsersWords[0], standardUser);
        exercise.setUuid(uuid);
        Mockito.when(exerciseRepository.findById(uuid))
                .thenReturn(Optional.of(exercise));
        ArgumentCaptor<Exercise> savedExercise = ArgumentCaptor.forClass(Exercise.class);
        CheckAnswerDtoResponse response = exerciseService.checkAnswer(
                new CheckAnswerDtoRequest(uuid, "wrong translation")
        );
        Assertions.assertEquals(AnswerStatus.INCORRECT, response.getStatus());
        Assertions.assertEquals(standardUsersWords[0].getWord().getTranslation(), response.getTranslation());
        Mockito.verify(exerciseRepository, Mockito.times(1))
                .save(savedExercise.capture());
        Assertions.assertEquals(ExerciseStatus.WRONG_ANSWER, savedExercise.getValue().getStatus());
        Assertions.assertNotNull(savedExercise.getValue().getAnswerDatetime());
        Assertions.assertEquals(uuid, savedExercise.getValue().getUuid());
        Assertions.assertEquals(standardUser, savedExercise.getValue().getUser());

        UsersWord savedUsersWord = savedExercise.getValue().getUsersWord();
        Assertions.assertEquals(standardUsersWords[0].getId(), savedUsersWord.getId());
        Assertions.assertEquals(0, savedUsersWord.getLastInterval());

        Mockito.verify(statisticsRepository, Mockito.times(1))
                .save(new Statistics(0, standardUser, 0, 1, LocalDate.now()));
    }

    @Test
    void whenExerciseNotExists_checkAnswer_shouldThrowException() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(statisticsRepository.findByUserIdAndDate(standardUser.getId(), LocalDate.now()))
                .thenReturn(Optional.empty());

        Mockito.when(exerciseRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        try {
            exerciseService.checkAnswer(
                    new CheckAnswerDtoRequest(UUID.randomUUID(),
                            "translation"));
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.EXERCISE_NOT_EXISTS, e.getErrorCode());
        }

        Mockito.verify(exerciseRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(statisticsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void whenLastIntervalIsMaxAndAnswerIsCorrect_checkAnswer_shouldNotChangeInterval() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(statisticsRepository.findByUserIdAndDate(standardUser.getId(), LocalDate.now()))
                .thenReturn(Optional.empty());

        UUID uuid = java.util.UUID.randomUUID();
        int lastInterval = exerciseService.getFibLength() - 1;
        standardUsersWords[0].setLastInterval(lastInterval);
        Exercise exercise = new Exercise(standardUsersWords[0], standardUser);
        exercise.setUuid(uuid);
        Mockito.when(exerciseRepository.findById(uuid))
                .thenReturn(Optional.of(exercise));
        ArgumentCaptor<Exercise> savedExercise = ArgumentCaptor.forClass(Exercise.class);
        exerciseService.checkAnswer(
                new CheckAnswerDtoRequest(uuid, getSimpleTranslation(standardUsersWords[0].getWord()))
        );
        Mockito.verify(exerciseRepository, Mockito.times(1))
                .save(savedExercise.capture());

        UsersWord savedUsersWord = savedExercise.getValue().getUsersWord();
        Assertions.assertEquals(lastInterval, savedUsersWord.getLastInterval());

        Mockito.verify(statisticsRepository, Mockito.times(1))
                .save(new Statistics(0, standardUser, 1, 0, LocalDate.now()));
    }

    @Test
    void whenLastIntervalIsMinAndAnswerIsWrong_checkAnswer_shouldNotChangeInterval() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(statisticsRepository.findByUserIdAndDate(standardUser.getId(), LocalDate.now()))
                .thenReturn(Optional.empty());

        UUID uuid = java.util.UUID.randomUUID();
        int lastInterval = 0;
        standardUsersWords[0].setLastInterval(lastInterval);
        Exercise exercise = new Exercise(standardUsersWords[0], standardUser);
        exercise.setUuid(uuid);
        Mockito.when(exerciseRepository.findById(uuid))
                .thenReturn(Optional.of(exercise));
        ArgumentCaptor<Exercise> savedExercise = ArgumentCaptor.forClass(Exercise.class);
        exerciseService.checkAnswer(
                new CheckAnswerDtoRequest(uuid, "wrong translation")
        );
        Mockito.verify(exerciseRepository, Mockito.times(1))
                .save(savedExercise.capture());

        UsersWord savedUsersWord = savedExercise.getValue().getUsersWord();
        Assertions.assertEquals(lastInterval, savedUsersWord.getLastInterval());

        Mockito.verify(statisticsRepository, Mockito.times(1))
                .save(new Statistics(0, standardUser, 0, 1, LocalDate.now()));
    }
}
