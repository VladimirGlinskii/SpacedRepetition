package net.thumbtack.spaced.repetition.mock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.commons.UserControllerUtils;
import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.request.user.LoginDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.user.LoginDtoResponse;
import net.thumbtack.spaced.repetition.model.Role;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.model.enums.RoleName;
import net.thumbtack.spaced.repetition.service.DictionaryService;
import net.thumbtack.spaced.repetition.service.ExerciseService;
import net.thumbtack.spaced.repetition.service.UserService;
import net.thumbtack.spaced.repetition.service.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.HashSet;

@AutoConfigureMockMvc()
@ActiveProfiles("test")
public class ControllerTestBase {

    protected User standardUser =
            new User(
                    1, "Username",
                    "username@gmail.com", "password",
                    Collections.singleton(new Role(1, RoleName.ROLE_USER))
            );

    protected User admin =
            new User(
                    2, "admin",
                    "admin@gmail.com", "admin",
                    new HashSet<Role>() {{
                        add(new Role(1, RoleName.ROLE_USER));
                        add(new Role(2, RoleName.ROLE_ADMIN));
                    }}
            );

    protected Word[] words = {
            new Word(1, "word1", "[\"transl1\"]"),
            new Word(2, "word2", "[\"transl2\"]"),
            new Word(3, "word3", "[\"transl3\"]"),
            new Word(4, "word4", "[\"transl4\"]"),
    };

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ApplicationProperties properties;

    @MockBean
    protected UserService userService;

    @MockBean
    protected WordService wordService;

    @MockBean
    protected DictionaryService dictionaryService;

    @MockBean
    protected ExerciseService exerciseService;

    @BeforeEach
    public void clear() {
        Mockito.reset(userService, wordService, dictionaryService, exerciseService);
    }

    protected <T> T getResponse(MvcResult mvcResult, Class<T> clazz) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
        } catch (Exception e) {
            return null;
        }
    }

    protected <T> String toJson(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "";
        }
    }

    protected String mockedLogin(User user) throws Exception {
        Mockito.when(userService.findByUsername(user.getUsername()))
                .thenReturn(user);
        Mockito.when(userService.loadUserByUsername(user.getUsername()))
                .thenReturn(user);
        return getResponse(UserControllerUtils.login(mockMvc, toJson(
                new LoginDtoRequest(user.getUsername(), user.getPassword()))),
                LoginDtoResponse.class).getToken();
    }

}
