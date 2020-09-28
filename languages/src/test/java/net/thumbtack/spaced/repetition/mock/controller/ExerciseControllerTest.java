package net.thumbtack.spaced.repetition.mock.controller;

import net.thumbtack.spaced.repetition.commons.ExerciseControllerUtils;
import net.thumbtack.spaced.repetition.dto.enums.AnswerStatus;
import net.thumbtack.spaced.repetition.dto.request.exercise.CheckAnswerDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.ErrorsResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.CheckAnswerDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.ExerciseDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
class ExerciseControllerTest extends ControllerTestBase {

    @Test
    void whenLogged_getExercise_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        ExerciseDtoResponse exercise = new ExerciseDtoResponse(
                UUID.randomUUID(), words[0].getWord()
        );
        Mockito.when(exerciseService.getExercise())
                .thenReturn(exercise);
        MvcResult result = ExerciseControllerUtils.getExercise(mockMvc, token);
        Assertions.assertEquals(200, result.getResponse().getStatus());
        ExerciseDtoResponse response = getResponse(result, ExerciseDtoResponse.class);
        Assertions.assertEquals(exercise.getUuid(), response.getUuid());
        Assertions.assertEquals(exercise.getWord(), response.getWord());
    }

    @Test
    void whenNotLogged_getExercise_shouldReturnForbiddenStatus() {
        try {
            MvcResult result = mockMvc
                    .perform(
                            get("/api/exercises")
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    ).andReturn();
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenExerciseExists_checkAnswer_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        UUID uuid = UUID.randomUUID();
        CheckAnswerDtoRequest request = new CheckAnswerDtoRequest(
                uuid, "translation"
        );
        Mockito.when(exerciseService.checkAnswer(request))
                .thenReturn(new CheckAnswerDtoResponse(AnswerStatus.CORRECT, "[\"translation\"]"));
        MvcResult result = ExerciseControllerUtils.checkAnswer(
                mockMvc, token, toJson(request));
        Assertions.assertEquals(200, result.getResponse().getStatus());
        CheckAnswerDtoResponse response = getResponse(result, CheckAnswerDtoResponse.class);
        Assertions.assertEquals(AnswerStatus.CORRECT, response.getStatus());
    }

    @Test
    void whenExerciseNotExists_checkAnswer_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(standardUser);
        UUID uuid = UUID.randomUUID();
        CheckAnswerDtoRequest request = new CheckAnswerDtoRequest(
                uuid, "translation"
        );
        Mockito.when(exerciseService.checkAnswer(request))
                .thenThrow(new ServerRuntimeException(ErrorCode.EXERCISE_NOT_EXISTS));
        MvcResult result = ExerciseControllerUtils.checkAnswer(
                mockMvc, token, toJson(request));
        Assertions.assertEquals(400, result.getResponse().getStatus());
        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(ErrorCode.EXERCISE_NOT_EXISTS, response.getErrors().get(0).getErrorCode());
    }

}
