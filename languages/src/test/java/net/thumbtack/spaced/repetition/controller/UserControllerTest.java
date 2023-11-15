package net.thumbtack.spaced.repetition.controller;

import net.thumbtack.spaced.repetition.commons.UserControllerUtils;
import net.thumbtack.spaced.repetition.dto.request.user.LoginDtoRequest;
import net.thumbtack.spaced.repetition.dto.request.user.RegisterDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.user.LoginDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.RegisterDtoResponse;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@TestPropertySource("classpath:application-integration.properties")
public class UserControllerTest extends ControllerTestBase {
    @Test
    public void register_shouldReturnOkStatus() throws Exception {
        RegisterDtoRequest registerDtoRequest =
                new RegisterDtoRequest("user", "user@email.com", "password");
        MvcResult result = UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        Assertions.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void whenUsernameAlreadyExists_register_shouldReturnBadRequestStatus() throws Exception {
        RegisterDtoRequest registerDtoRequest =
                new RegisterDtoRequest("user", "user@email.com", "password");
        UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        registerDtoRequest.setEmail("another@email.com");
        MvcResult result = UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        Assertions.assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void whenEmailAlreadyExists_register_shouldReturnBadRequestStatus() throws Exception {
        RegisterDtoRequest registerDtoRequest =
                new RegisterDtoRequest("user1", "user@email.com", "password");
        UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        registerDtoRequest.setUsername("another");
        MvcResult result = UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        Assertions.assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void whenUserRegistered_login_shouldReturnOkStatusAndToken() throws Exception {
        RegisterDtoRequest registerDtoRequest =
                new RegisterDtoRequest("user", "user@email.com", "password");
        UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        LoginDtoRequest loginRequest = new LoginDtoRequest("user", "password");
        MvcResult result = UserControllerUtils.login(mockMvc, toJson(loginRequest));
        Assertions.assertEquals(200, result.getResponse().getStatus());
        String token = getResponse(result, LoginDtoResponse.class).getToken();
        Assertions.assertNotNull(token);
        Assertions.assertNotEquals("", token);
    }

    @Test
    public void whenPasswordIncorrect_login_shouldReturnBadRequestStatus() throws Exception {
        RegisterDtoRequest registerDtoRequest =
                new RegisterDtoRequest("user", "user@email.com", "password");
        UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        LoginDtoRequest dtoRequest = new LoginDtoRequest("user", "another");
        MvcResult result = UserControllerUtils.login(mockMvc, toJson(dtoRequest));
        Assertions.assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void addUsersWord_shouldReturnOkStatus() throws Exception {
        Word word = wordRepository.save(new Word("word1", "[\"translation1\",\"translation2\"]"));
        RegisterDtoRequest registerDtoRequest =
                new RegisterDtoRequest("user", "user@email.com", "password");
        UserControllerUtils
                .register(mockMvc, toJson(registerDtoRequest));
        String request = toJson(new LoginDtoRequest("user", "password"));
        MvcResult result = UserControllerUtils.login(mockMvc, request);
        String token = getResponse(result, LoginDtoResponse.class).getToken();
        MvcResult addUsersWordMvc = UserControllerUtils.addUsersWord(mockMvc, token, word.getId());
        Assertions.assertEquals(200, addUsersWordMvc.getResponse().getStatus());
    }

    @Test
    public void whenUsersWordDisabled_addUsersWord_shouldActivateWordAndReturnOkStatus() throws Exception {
        Word word = wordRepository.save(new Word("word1", "[\"translation1\",\"translation2\"]"));
        RegisterDtoRequest registerDtoRequest =
                new RegisterDtoRequest("user", "user@email.com", "password");
        RegisterDtoResponse registerResp = getResponse(
                UserControllerUtils
                        .register(mockMvc, toJson(registerDtoRequest)),
                RegisterDtoResponse.class);
        String loginRequest = toJson(new LoginDtoRequest("user", "password"));
        MvcResult result = UserControllerUtils.login(mockMvc, loginRequest);
        String token =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        LoginDtoResponse.class).getToken();
        UserControllerUtils.addUsersWord(mockMvc, token, word.getId());
        UsersWord usersWord = usersWordRepository.findByUserIdAndWordId(registerResp.getId(), word.getId());
        usersWord.setStatus(UsersWordStatus.DISABLED);
        usersWordRepository.save(usersWord);
        MvcResult addUsersWordMvc = UserControllerUtils.addUsersWord(mockMvc, token, word.getId());
        Assertions.assertEquals(200, addUsersWordMvc.getResponse().getStatus());
        Assertions.assertEquals(
                UsersWordStatus.ACTIVE,
                usersWordRepository.findById(usersWord.getId()).get().getStatus()
        );
    }

    @Test
    public void whenUsersWordActive_addUsersWord_shouldDeactivateWordAndReturnOkStatus() throws Exception {
        Word word = wordRepository.save(new Word("word1", "[\"translation1\",\"translation2\"]"));
        String token = UserControllerUtils
                .registerUserAndGetToken(
                        mockMvc,
                        "user",
                        "user@email.com",
                        "password",
                        objectMapper);
        User user = userRepository.findByUsernameIgnoreCase("user");
        UserControllerUtils.addUsersWord(mockMvc, token, word.getId());
        UsersWord usersWord = usersWordRepository.findByUserIdAndWordId(user.getId(), word.getId());
        MvcResult deleteUsersWordMvc = UserControllerUtils.deleteUsersWord(mockMvc, token, word.getId());
        Assertions.assertEquals(200, deleteUsersWordMvc.getResponse().getStatus());
        Assertions.assertEquals(
                UsersWordStatus.DISABLED,
                usersWordRepository.findById(usersWord.getId()).get().getStatus()
        );
    }

}
