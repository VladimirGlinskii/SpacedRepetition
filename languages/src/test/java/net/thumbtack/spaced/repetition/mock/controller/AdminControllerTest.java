package net.thumbtack.spaced.repetition.mock.controller;

import net.thumbtack.spaced.repetition.commons.AdminControllerUtils;
import net.thumbtack.spaced.repetition.dto.request.dictionary.AddDictionaryRequest;
import net.thumbtack.spaced.repetition.dto.request.dictionary.RenameDictionaryRequest;
import net.thumbtack.spaced.repetition.dto.request.word.AddWordRequest;
import net.thumbtack.spaced.repetition.dto.request.word.EditWordRequest;
import net.thumbtack.spaced.repetition.dto.response.ErrorResponse;
import net.thumbtack.spaced.repetition.dto.response.ErrorsResponse;
import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionaryDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.UsersDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.word.AddWordResponse;
import net.thumbtack.spaced.repetition.dto.response.word.WordsDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.utils.ErrorMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
class AdminControllerTest extends ControllerTestBase {

    @Test
    void whenNotAdmin_getAllUsers_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = AdminControllerUtils.getAllUsers(mockMvc, token, 0, properties.getMaxPageSize(), "");
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_getAllUsers_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        Mockito.when(userService.getAll(1, properties.getMaxPageSize(), "filter"))
                .thenReturn(new UsersDtoResponse(
                        new ArrayList<>(), 2
                ));
        MvcResult result = AdminControllerUtils.getAllUsers(mockMvc, token, 1, properties.getMaxPageSize(), "filter");
        Mockito.verify(userService, Mockito.times(1))
                .getAll(1, properties.getMaxPageSize(), "filter");
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void whenNotAdmin_getAllWords_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = AdminControllerUtils.getAllWords(mockMvc, token, 0, properties.getMaxPageSize(), "");
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_getAllWords_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        Mockito.when(wordService.getAll(1, properties.getMaxPageSize(), "filter"))
                .thenReturn(new WordsDtoResponse(
                        new ArrayList<>(), 2
                ));
        MvcResult result = AdminControllerUtils.getAllWords(mockMvc, token, 1, properties.getMaxPageSize(), "filter");
        Mockito.verify(wordService, Mockito.times(1))
                .getAll(1, properties.getMaxPageSize(), "filter");
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void whenNotAdmin_addWord_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        AddWordRequest request = new AddWordRequest(
                words[0].getWord(), words[0].getTranslation());
        MvcResult result = AdminControllerUtils.addWord(mockMvc, token, toJson(request));
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_addWord_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        AddWordRequest request = new AddWordRequest(
                words[0].getWord(), words[0].getTranslation());
        AddWordResponse expectedResponse = new AddWordResponse(words[0].getId(), words[0].getWord());
        Mockito.when(wordService.addWord(request, 0))
                .thenReturn(expectedResponse);
        MvcResult result = AdminControllerUtils.addWord(mockMvc, token, toJson(request));
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        AddWordResponse response = getResponse(result, AddWordResponse.class);
        Assertions.assertEquals(expectedResponse.getId(), response.getId());
        Assertions.assertEquals(expectedResponse.getWord(), response.getWord());
    }

    @Test
    void whenNotAdmin_editWord_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        EditWordRequest request = new EditWordRequest(
                words[0].getWord(), words[0].getTranslation());
        MvcResult result = AdminControllerUtils.editWord(mockMvc, token, toJson(request), words[0].getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_editWord_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        EditWordRequest request = new EditWordRequest(
                words[0].getWord(), words[0].getTranslation());
        MvcResult result = AdminControllerUtils.editWord(mockMvc, token, toJson(request), words[0].getId());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(wordService, Mockito.times(1))
                .editWord(request, words[0].getId());
    }

    @Test
    void whenNotAdmin_deleteWord_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = AdminControllerUtils.deleteWord(mockMvc, token, words[0].getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_deleteWord_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils.deleteWord(mockMvc, token, words[0].getId());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(wordService, Mockito.times(1))
                .deleteWord(words[0].getId());
    }

    @Test
    void whenNotAdmin_deleteUser_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = AdminControllerUtils.deleteUser(mockMvc, token, standardUser.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_deleteUser_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils.deleteUser(mockMvc, token, standardUser.getId());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(userService, Mockito.times(1))
                .deleteUser(standardUser.getId());
    }

    @Test
    void whenAdmin_addDictionary_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        AddDictionaryRequest request = new AddDictionaryRequest("dictionary");
        DictionaryDtoResponse expectedResponse = new DictionaryDtoResponse(
                1, request.getName());
        Mockito.when(dictionaryService.addDictionary(request))
                .thenReturn(expectedResponse);
        MvcResult result = AdminControllerUtils.addDictionary(mockMvc, token, toJson(request));
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        DictionaryDtoResponse response = getResponse(result, DictionaryDtoResponse.class);
        Assertions.assertEquals(expectedResponse.getId(), response.getId());
        Assertions.assertEquals(expectedResponse.getName(), response.getName());
    }

    @Test
    void whenAdmin_addWordToDictionary_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils
                .addWordToDictionary(mockMvc, token, 1, 2);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(dictionaryService, Mockito.times(1))
                .addWord(1, 2);
    }

    @Test
    void whenDictionaryNotExists_addWordToDictionary_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(admin);
        Mockito.doThrow(new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID))
                .when(dictionaryService).addWord(1, 2);
        MvcResult result = AdminControllerUtils
                .addWordToDictionary(mockMvc, token, 1, 2);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, response.getErrors().get(0).getErrorCode());
    }

    @Test
    void whenAdmin_deleteWordFromDictionary_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils
                .deleteWordFromDictionary(mockMvc, token, 1, 2);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(dictionaryService, Mockito.times(1))
                .deleteWord(1, 2);
    }

    @Test
    void whenDictionaryNotExists_deleteWordFromDictionary_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(admin);
        Mockito.doThrow(new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID))
                .when(dictionaryService).deleteWord(1, 2);
        MvcResult result = AdminControllerUtils
                .deleteWordFromDictionary(mockMvc, token, 1, 2);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, response.getErrors().get(0).getErrorCode());
    }

    @Test
    void whenDtoRequestNotValid_addWord_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils
                .addWord(mockMvc, token, toJson(new AddWordRequest(null, null)));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(2, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().containsAll(Arrays.asList(
                new ErrorResponse(ErrorCode.FIELD_NOT_VALID, ErrorMessages.EMPTY_WORD_NAME, "word"),
                new ErrorResponse(ErrorCode.FIELD_NOT_VALID, ErrorMessages.EMPTY_WORD_TRANSLATION, "translation")
        )));
    }

    @Test
    void whenDtoRequestNotValid_addDictionary_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils
                .addDictionary(mockMvc, token, toJson(new AddDictionaryRequest(null)));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors()
                .contains(new ErrorResponse(
                        ErrorCode.FIELD_NOT_VALID,
                        ErrorMessages.EMPTY_DICTIONARY_NAME,
                        "name")));
    }

    @Test
    void whenNotAdmin_deleteDictionary_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = AdminControllerUtils.deleteDictionary(mockMvc, token, 1);
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_deleteDictionary_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils.deleteDictionary(mockMvc, token, 1);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(dictionaryService, Mockito.times(1))
                .deleteDictionary(1);
    }

    @Test
    void whenAdmin_uploadWordsFromFile_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        MvcResult result = AdminControllerUtils.uploadWordsFromFile(mockMvc, token,
                "word, transl", 1);
        ArgumentCaptor<MultipartFile> file = ArgumentCaptor.forClass(MultipartFile.class);
        Mockito.verify(wordService, Mockito.times(1))
                .uploadWordsFromFile(file.capture(), Mockito.anyInt());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertArrayEquals("word, transl".getBytes(), file.getValue().getBytes());
    }

    @Test
    void whenNotAdmin_uploadWordsFromFile_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        MvcResult result = AdminControllerUtils.uploadWordsFromFile(mockMvc, token,
                "word, transl", 1);
        Mockito.verify(wordService, Mockito.never())
                .uploadWordsFromFile(Mockito.any(), Mockito.anyInt());
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenNotAdmin_renameDictionary_shouldReturnForbiddenStatus() throws Exception {
        String token = mockedLogin(standardUser);
        RenameDictionaryRequest request = new RenameDictionaryRequest("newName");
        MvcResult result = AdminControllerUtils.renameDictionary(mockMvc, token, toJson(request), 1);
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    void whenAdmin_renameDictionary_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(admin);
        RenameDictionaryRequest request = new RenameDictionaryRequest("newName");
        MvcResult result = AdminControllerUtils.renameDictionary(mockMvc, token, toJson(request), 1);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(dictionaryService, Mockito.times(1))
                .renameDictionary(1, request);
    }
}
