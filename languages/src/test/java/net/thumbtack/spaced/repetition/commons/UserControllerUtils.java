package net.thumbtack.spaced.repetition.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.dto.request.user.LoginDtoRequest;
import net.thumbtack.spaced.repetition.dto.request.user.RegisterDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.user.LoginDtoResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class UserControllerUtils {
    public static final String AUTHORIZATION = "Authorization";

    public static MvcResult register(MockMvc mockMvc, String request) throws Exception {
        return mockMvc.perform(
                post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andReturn();
    }

    public static MvcResult login(MockMvc mockMvc, String request) throws Exception {
        return mockMvc.perform(
                put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andReturn();
    }

    public static String registerUserAndGetToken(MockMvc mockMvc,
                                                 String username,
                                                 String email,
                                                 String password,
                                                 ObjectMapper objectMapper) throws Exception {
        RegisterDtoRequest registerRequest = new RegisterDtoRequest(username, email, password);
        register(mockMvc, objectMapper.writeValueAsString(registerRequest));
        LoginDtoRequest loginRequest = new LoginDtoRequest(username, password);
        return objectMapper
                .readValue(
                        login(mockMvc, objectMapper
                                .writeValueAsString(loginRequest))
                                .getResponse().getContentAsString(), LoginDtoResponse.class)
                .getToken();
    }

    public static MvcResult addUsersWord(MockMvc mockMvc, String token, int wordId) throws Exception {
        return mockMvc
                .perform(
                        post("/api/users/words/" + wordId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, token)
                ).andReturn();
    }

    public static MvcResult deleteUsersWord(MockMvc mockMvc, String token, int wordId) throws Exception {
        return mockMvc
                .perform(
                        put("/api/users/words/" + wordId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, token)
                ).andReturn();
    }

    public static MvcResult getWeeklyStatistics(MockMvc mockMvc, String token) throws Exception {
        return mockMvc.perform(
                get("/api/users/statistics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult changePassword(MockMvc mockMvc, String token, String request) throws Exception {
        return mockMvc.perform(
                put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult selectWholeDictionary(MockMvc mockMvc, String token, int dictionaryId) throws Exception {
        return mockMvc.perform(
                post(String.format("/api/users/dictionaries/%d", dictionaryId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
        ).andReturn();
    }

    public static MvcResult unselectWholeDictionary(MockMvc mockMvc, String token, int dictionaryId) throws Exception {
        return mockMvc.perform(
                put(String.format("/api/users/dictionaries/%d", dictionaryId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
        ).andReturn();
    }
}
