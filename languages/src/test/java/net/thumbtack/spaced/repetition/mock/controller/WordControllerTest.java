package net.thumbtack.spaced.repetition.mock.controller;

import net.thumbtack.spaced.repetition.commons.WordControllerUtils;
import net.thumbtack.spaced.repetition.dto.response.ErrorsResponse;
import net.thumbtack.spaced.repetition.dto.response.word.GetWordsForDictionaryResponse;
import net.thumbtack.spaced.repetition.dto.response.word.PronunciationLinkResponse;
import net.thumbtack.spaced.repetition.dto.response.word.WordDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

@SpringBootTest
class WordControllerTest extends ControllerTestBase {

    @Test
    void whenUserLoggedAndShowAll_getWordsForDictionary_shouldGetAllWordsAndReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        GetWordsForDictionaryResponse expectedResponse = new GetWordsForDictionaryResponse(
                Collections.singletonList(
                        new WordDtoResponse(
                                words[0].getId(),
                                words[0].getWord(),
                                words[0].getTranslation(),
                                true)), 1);
        Mockito.when(wordService.getWordsForDictionary(1, 0, properties.getMaxPageSize(), "filter"))
                .thenReturn(expectedResponse);
        MvcResult result = WordControllerUtils
                .getWordsForDictionary(mockMvc, token, 1, 0,
                        properties.getMaxPageSize(), true, "filter");
        Assertions.assertEquals(200, result.getResponse().getStatus());
        GetWordsForDictionaryResponse response = getResponse(result, GetWordsForDictionaryResponse.class);
        Assertions.assertEquals(expectedResponse.getWords(), response.getWords());
    }

    @Test
    void whenUserLoggedAndNotShowAll_getWordsForDictionary_shouldGetDictionaryWordsAndReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        GetWordsForDictionaryResponse expectedResponse = new GetWordsForDictionaryResponse(
                Collections.singletonList(
                        new WordDtoResponse(
                                words[0].getId(),
                                words[0].getWord(),
                                words[0].getTranslation(),
                                true)), 1);
        Mockito.when(wordService.getDictionaryWords(1, 0, properties.getMaxPageSize(), "filter"))
                .thenReturn(expectedResponse);
        MvcResult result = WordControllerUtils
                .getWordsForDictionary(mockMvc, token, 1, 0,
                        properties.getMaxPageSize(), false, "filter");
        Assertions.assertEquals(200, result.getResponse().getStatus());
        GetWordsForDictionaryResponse response = getResponse(result, GetWordsForDictionaryResponse.class);
        Assertions.assertEquals(expectedResponse.getWords(), response.getWords());
    }

    @Test
    void whenDictionaryNotExists_getWordsForDictionary_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(standardUser);
        Mockito.when(wordService.getWordsForDictionary(1, 0, properties.getMaxPageSize(), "filter"))
                .thenThrow(new ServerRuntimeException(ErrorCode.WRONG_DICTIONARY_ID));
        MvcResult result = WordControllerUtils
                .getWordsForDictionary(mockMvc, token, 1, 0,
                        properties.getMaxPageSize(), true, "filter");
        Assertions.assertEquals(400, result.getResponse().getStatus());
        ErrorsResponse response = getResponse(result, ErrorsResponse.class);
        Assertions.assertEquals(ErrorCode.WRONG_DICTIONARY_ID, response.getErrors().get(0).getErrorCode());
    }

    @Test
    void whenUserLoggedAndUrlFound_getWordPronunciationLink_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        Mockito.when(wordService.getWordPronunciationLink("word"))
                .thenReturn(new PronunciationLinkResponse("url"));
        MvcResult result = WordControllerUtils.getWordPronunciationLink(mockMvc, token, "word");
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertEquals(
                "url",
                getResponse(result, PronunciationLinkResponse.class).getUrl());
    }

    @Test
    void whenUserLoggedAndUrlNotFound_getWordPronunciationLink_shouldReturnBadRequest() throws Exception {
        String token = mockedLogin(standardUser);
        Mockito.when(wordService.getWordPronunciationLink("word"))
                .thenThrow(new ServerRuntimeException(ErrorCode.CANT_FIND_PRONUNCIATION));
        MvcResult result = WordControllerUtils.getWordPronunciationLink(mockMvc, token, "word");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        Assertions.assertEquals(
                ErrorCode.CANT_FIND_PRONUNCIATION,
                getResponse(result, ErrorsResponse.class).getErrors().get(0).getErrorCode());
    }
}
