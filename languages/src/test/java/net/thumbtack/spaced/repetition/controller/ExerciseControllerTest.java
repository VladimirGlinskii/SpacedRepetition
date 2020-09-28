package net.thumbtack.spaced.repetition.controller;

import net.thumbtack.spaced.repetition.commons.ExerciseControllerUtils;
import net.thumbtack.spaced.repetition.commons.UserControllerUtils;
import net.thumbtack.spaced.repetition.dto.enums.AnswerStatus;
import net.thumbtack.spaced.repetition.dto.request.exercise.CheckAnswerDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.exercise.CheckAnswerDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.ExerciseDtoResponse;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
class ExerciseControllerTest extends ControllerTestBase {

    @Test
    void getExercise_shouldReturnExerciseDtoResponse() throws Exception {
        Word word = wordRepository.save(new Word("word1", "[\"translation1\",\"translation2\"]"));
        String token = UserControllerUtils
                .registerUserAndGetToken(mockMvc, "user",
                        "user@gmail.com", "password", objectMapper);
        UserControllerUtils.addUsersWord(mockMvc, token, word.getId());
        MvcResult result = ExerciseControllerUtils.getExercise(mockMvc, token);
        Assertions.assertEquals(200, result.getResponse().getStatus());
        ExerciseDtoResponse response =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(), ExerciseDtoResponse.class
                );
        Assertions.assertNotNull(response.getWord());
        Assertions.assertNotNull(response.getUuid());
    }

    @Test
    void whenEmptyUsersWords_getExercise_shouldReturnExerciseWithNulls() throws Exception {
        String token = UserControllerUtils
                .registerUserAndGetToken(mockMvc, "user",
                        "user@gmail.com", "password", objectMapper);
        MvcResult result = ExerciseControllerUtils.getExercise(mockMvc, token);
        Assertions.assertEquals(200, result.getResponse().getStatus());
        ExerciseDtoResponse response = getResponse(result, ExerciseDtoResponse.class);
        Assertions.assertNull(response.getWord());
        Assertions.assertNull(response.getUuid());
    }

    @Test
    void whenTranslationIsRight_checkAnswer_shouldReturnCorrectAnswerStatusAndIncreaseInterval()
            throws Exception {
        String token = UserControllerUtils
                .registerUserAndGetToken(mockMvc, "user",
                        "user@gmail.com", "password", objectMapper);
        User user = userRepository.findByUsernameIgnoreCase("user");

        Word word = wordRepository.save(new Word("word1", "[\"translation1\",\"translation2\"]"));
        UserControllerUtils.addUsersWord(mockMvc, token, word.getId());
        MvcResult result = ExerciseControllerUtils.getExercise(mockMvc, token);
        ExerciseDtoResponse exerciseDtoResponse = getResponse(result, ExerciseDtoResponse.class);
        CheckAnswerDtoRequest checkAnswerDtoRequest =
                new CheckAnswerDtoRequest(exerciseDtoResponse.getUuid(),
                        "translation2");
        MvcResult result2 = ExerciseControllerUtils
                .checkAnswer(mockMvc, token, toJson(checkAnswerDtoRequest));
        Assertions.assertEquals(200, result2.getResponse().getStatus());
        CheckAnswerDtoResponse response = getResponse(result2, CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.CORRECT, response.getStatus());
        UsersWord usersWord = usersWordRepository.findByUserIdAndWordId(user.getId(), word.getId());
        Assertions.assertEquals(1, usersWord.getLastInterval());

    }

    @Test
    void whenTranslationIsWrong_checkAnswer_shouldReturnIncorrectAnswerStatus() throws Exception {
        Word word = wordRepository.save(new Word("word1", "[\"translation1\",\"translation2\"]"));
        String token = UserControllerUtils
                .registerUserAndGetToken(mockMvc, "user",
                        "user@gmail.com", "password", objectMapper);
        UserControllerUtils.addUsersWord(mockMvc, token, word.getId());
        MvcResult result = ExerciseControllerUtils.getExercise(mockMvc, token);
        ExerciseDtoResponse exerciseDtoResponse = getResponse(result, ExerciseDtoResponse.class);
        CheckAnswerDtoRequest checkAnswerDtoRequest =
                new CheckAnswerDtoRequest(exerciseDtoResponse.getUuid(),
                        "%wrong translation%");
        MvcResult result2 = ExerciseControllerUtils
                .checkAnswer(mockMvc, token,
                        toJson(checkAnswerDtoRequest));
        Assertions.assertEquals(200, result2.getResponse().getStatus());
        CheckAnswerDtoResponse response = getResponse(result2, CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.INCORRECT, response.getStatus());
    }
}
