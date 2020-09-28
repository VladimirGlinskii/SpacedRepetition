package net.thumbtack.spaced.repetition.commons;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class ExerciseControllerUtils {

    public static final String AUTHORIZATION = "Authorization";

    public static MvcResult getExercise(MockMvc mockMvc, String token) throws Exception {
        return mockMvc
                .perform(
                        get("/api/exercises")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(AUTHORIZATION, token)
                ).andReturn();
    }

    public static MvcResult checkAnswer(MockMvc mockMvc, String token, String request) throws Exception {
        return mockMvc
                .perform(
                        put("/api/exercises")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(AUTHORIZATION, token)
                                .content(request)
                ).andReturn();
    }
}
