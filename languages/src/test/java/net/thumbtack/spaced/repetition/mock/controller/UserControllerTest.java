package net.thumbtack.spaced.repetition.mock.controller;

import net.thumbtack.spaced.repetition.commons.UserControllerUtils;
import net.thumbtack.spaced.repetition.dto.request.user.ChangePasswordRequest;
import net.thumbtack.spaced.repetition.dto.request.user.LoginDtoRequest;
import net.thumbtack.spaced.repetition.dto.request.user.RegisterDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.ErrorResponse;
import net.thumbtack.spaced.repetition.dto.response.ErrorsResponse;
import net.thumbtack.spaced.repetition.dto.response.user.LoginDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.RegisterDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.utils.ErrorMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

@SpringBootTest
public class UserControllerTest extends ControllerTestBase {

    @Test
    void register_shouldReturnOkStatus() throws Exception {
        RegisterDtoRequest request = new RegisterDtoRequest(
                standardUser.getUsername(),
                standardUser.getEmail(),
                standardUser.getPassword()
        );
        Mockito.when(userService.register(request))
                .thenReturn(new RegisterDtoResponse(1, standardUser.getUsername()));
        MvcResult result = UserControllerUtils.register(mockMvc, objectMapper.writeValueAsString(request));
        Assertions.assertEquals(200, result.getResponse().getStatus());

        RegisterDtoResponse response = getResponse(result, RegisterDtoResponse.class);

        Assertions.assertEquals(1, response.getId());
        Assertions.assertEquals(standardUser.getUsername(), response.getUsername());
    }

    @Test
    void whenUsernameExists_register_shouldReturnBadRequest() throws Exception {
        RegisterDtoRequest request = new RegisterDtoRequest(
                standardUser.getUsername(),
                standardUser.getEmail(),
                standardUser.getPassword()
        );
        Mockito.when(userService.register(request))
                .thenThrow(new ServerRuntimeException(ErrorCode.USERNAME_ALREADY_EXISTS));
        MvcResult result = UserControllerUtils.register(mockMvc, objectMapper.writeValueAsString(request));
        Assertions.assertEquals(400, result.getResponse().getStatus());

        ErrorsResponse response = getResponse(result, ErrorsResponse.class);

        Assertions.assertEquals(ErrorCode.USERNAME_ALREADY_EXISTS, response.getErrors().get(0).getErrorCode());
    }

    @Test
    void whenRegistered_login_shouldReturnOkStatus() throws Exception {
        Mockito.when(userService.findByUsername(standardUser.getUsername()))
                .thenReturn(standardUser);
        Mockito.when(userService.loadUserByUsername(standardUser.getUsername()))
                .thenReturn(standardUser);
        MvcResult result = UserControllerUtils.login(mockMvc, toJson(
                new LoginDtoRequest(standardUser.getUsername(), standardUser.getPassword())));
        Assertions.assertEquals(200, result.getResponse().getStatus());

        LoginDtoResponse response = getResponse(result, LoginDtoResponse.class);
        Assertions.assertEquals(standardUser.getId(), response.getId());
        Assertions.assertNotNull(response.getToken());
        Assertions.assertNotEquals("", response.getToken());
    }

    @Test
    void whenNotRegistered_login_shouldReturnBadRequest() throws Exception {
        Mockito.when(userService.findByUsername(standardUser.getUsername()))
                .thenReturn(null);
        Mockito.when(userService.loadUserByUsername(standardUser.getUsername()))
                .thenReturn(null);
        MvcResult result = UserControllerUtils.login(mockMvc, toJson(
                new LoginDtoRequest(standardUser.getUsername(), standardUser.getPassword())));
        Assertions.assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    void whenLogged_addUsersWord_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = UserControllerUtils.addUsersWord(mockMvc, token, 1);
        Mockito.verify(userService, Mockito.times(1))
                .addUsersWord(1);
        Assertions.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenWordNotFound_addUsersWord_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(standardUser);
        Mockito.doThrow(new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND))
                .when(userService).addUsersWord(1);
        MvcResult result = UserControllerUtils.addUsersWord(mockMvc, token, 1);
        Assertions.assertEquals(400, result.getResponse().getStatus());

        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(ErrorCode.WORD_NOT_FOUND, response.getErrors().get(0).getErrorCode());
    }

    @Test
    void whenLogged_deleteUsersWord_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = UserControllerUtils.deleteUsersWord(mockMvc, token, 1);
        Mockito.verify(userService, Mockito.times(1))
                .deleteUsersWord(1);
        Assertions.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void whenDtoRequestNotValid_register_shouldReturnBadRequest() throws Exception {
        RegisterDtoRequest request = new RegisterDtoRequest(
                "12345",
                "email",
                "pass"
        );
        MvcResult result = UserControllerUtils.register(mockMvc, toJson(request));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(3, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().containsAll(Arrays.asList(
                new ErrorResponse(ErrorCode.FIELD_NOT_VALID, ErrorMessages.INCORRECT_USERNAME_PATTERN, "username"),
                new ErrorResponse(ErrorCode.FIELD_NOT_VALID, ErrorMessages.WRONG_EMAIL_PATTERN, "email"),
                new ErrorResponse(ErrorCode.FIELD_NOT_VALID, ErrorMessages.INCORRECT_PASSWORD_LENGTH, "password")
        )));
    }

    @Test
    public void whenLogged_getWeeklyStatistics_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = UserControllerUtils.getWeeklyStatistics(mockMvc, token);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void whenLoggedAndDtoValid_changePassword_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        ChangePasswordRequest request =
                new ChangePasswordRequest(standardUser.getId(), "oldPassword", "newPassword");
        MvcResult result = UserControllerUtils.changePassword(mockMvc, token, toJson(request));
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void whenLoggedAndDtoNotValid_changePassword_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(standardUser);
        ChangePasswordRequest request =
                new ChangePasswordRequest(standardUser.getId(), "sh", "sh");
        MvcResult result = UserControllerUtils.changePassword(mockMvc, token, toJson(request));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorsResponse response = getResponse(result, ErrorsResponse.class);

        Assertions.assertEquals(2, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().containsAll(Arrays.asList(
                new ErrorResponse(ErrorCode.FIELD_NOT_VALID, ErrorMessages.INCORRECT_PASSWORD_LENGTH, "oldPassword"),
                new ErrorResponse(ErrorCode.FIELD_NOT_VALID, ErrorMessages.INCORRECT_PASSWORD_LENGTH, "newPassword")
        )));
    }

    @Test
    public void whenLoggedAndDictionaryExists_selectWholeDictionary_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = UserControllerUtils.selectWholeDictionary(mockMvc, token, 1);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(userService, Mockito.times(1))
                .selectWholeDictionary(1);
    }

    @Test
    public void whenLoggedAndDictionaryExists_unselectWholeDictionary_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = UserControllerUtils.unselectWholeDictionary(mockMvc, token, 1);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(userService, Mockito.times(1))
                .unselectWholeDictionary(1);
    }

}
