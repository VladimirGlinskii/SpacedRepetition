package net.thumbtack.spaced.repetition.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.enums.AnswerStatus;
import net.thumbtack.spaced.repetition.dto.request.exercise.CheckAnswerDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.exercise.CheckAnswerDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.ExerciseDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.model.*;
import net.thumbtack.spaced.repetition.model.enums.ExerciseStatus;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import net.thumbtack.spaced.repetition.repos.ExerciseRepository;
import net.thumbtack.spaced.repetition.repos.StatisticsRepository;
import net.thumbtack.spaced.repetition.repos.UsersWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static net.thumbtack.spaced.repetition.utils.ApplicationConstants.SET_TYPE;

@Service(value = "exerciseService")
public class ExerciseService {

    private ExerciseRepository exerciseRepository;

    private StatisticsRepository statisticsRepository;

    private UsersWordRepository usersWordRepository;

    private ObjectMapper objectMapper;

    private int[] fib;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository,
                           StatisticsRepository statisticsRepository,
                           UsersWordRepository usersWordRepository,
                           ApplicationProperties properties,
                           ObjectMapper objectMapper) {
        this.exerciseRepository = exerciseRepository;
        this.statisticsRepository = statisticsRepository;
        this.usersWordRepository = usersWordRepository;
        this.objectMapper = objectMapper;

        fib = new int[properties.getFibonacciSequenceLength()];
        fib[0] = 1;
        fib[1] = 2;
        for (int i = 2; i < properties.getFibonacciSequenceLength(); i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
    }

    public int getFibLength() {
        return fib.length;
    }

    public ExerciseDtoResponse getExercise() {
        User user = ServiceUtils.getUser();
        Exercise exercise = exerciseRepository
                .findTopByUserIdAndStatus(user.getId(), ExerciseStatus.NO_ANSWER);
        if (exercise != null && exercise.getUsersWord().getStatus() == UsersWordStatus.ACTIVE) {
            return new ExerciseDtoResponse(exercise.getUuid(), exercise.getUsersWord().getWord().getWord());
        }
        UsersWord usersWord = usersWordRepository
                .findTopByUserIdAndStatusOrderByNextDatetimeAsc(
                        ServiceUtils.getUser().getId(), UsersWordStatus.ACTIVE);
        if (usersWord == null) {
            return new ExerciseDtoResponse(null, null);
        }
        exercise = new Exercise(usersWord, user);
        exerciseRepository.save(exercise);
        return new ExerciseDtoResponse(exercise.getUuid(), usersWord.getWord().getWord());
    }

    @Transactional
    public CheckAnswerDtoResponse checkAnswer(CheckAnswerDtoRequest request) {
        Exercise exercise = exerciseRepository
                .findById(request.getUuid())
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.EXERCISE_NOT_EXISTS));
        UsersWord usersWord = exercise.getUsersWord();
        ExerciseStatus exerciseStatus = ExerciseStatus.WRONG_ANSWER;
        AnswerStatus answerStatus = AnswerStatus.INCORRECT;
        if (answerCorrect(usersWord.getWord(), request.getTranslation())) {
            exerciseStatus = ExerciseStatus.RIGHT_ANSWER;
            answerStatus = AnswerStatus.CORRECT;
        }
        exercise.setStatus(exerciseStatus);
        exercise.setAnswerDatetime(LocalDateTime.now());
        calculateNextDatetime(usersWord, exerciseStatus);
        exerciseRepository.save(exercise);
        User user = ServiceUtils.getUser();
        Statistics statistics = statisticsRepository
                .findByUserIdAndDate(user.getId(), LocalDate.now())
                .orElse(new Statistics(user, LocalDate.now()));
        if (answerStatus == AnswerStatus.CORRECT) {
            statistics.increaseCorrectAnswers();
        } else {
            statistics.increaseWrongAnswers();
        }
        statisticsRepository.save(statistics);
        return new CheckAnswerDtoResponse(answerStatus, usersWord.getWord().getTranslation());
    }

    private boolean answerCorrect(Word word, String answer) {
        try {
            Set<String> translations = objectMapper
                    .readValue(word.getTranslation(), SET_TYPE);
            return translations.stream().anyMatch(t -> t.equalsIgnoreCase(answer));
        } catch (JsonProcessingException e) {
            throw new ServerRuntimeException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    private void calculateNextDatetime(UsersWord usersWord, ExerciseStatus status) {
        int lastInterval = usersWord.getLastInterval();
        if (status == ExerciseStatus.RIGHT_ANSWER) {
            lastInterval++;
            if (lastInterval >= fib.length) {
                lastInterval = fib.length - 1;
            }
        } else {
            lastInterval--;
            if (lastInterval < 0) {
                lastInterval = 0;
            }
        }
        usersWord.setLastInterval(lastInterval);
        usersWord.setNextDatetime(LocalDateTime.now().plusSeconds(fib[lastInterval]));
    }
}
