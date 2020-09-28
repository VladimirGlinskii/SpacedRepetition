package net.thumbtack.spaced.repetition.mock.controller;

import net.thumbtack.spaced.repetition.commons.DictionaryControllerUtils;
import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionariesDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.word.GetDictionaryWordsDtoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashSet;

@SpringBootTest
class DictionaryControllerTest extends ControllerTestBase {

    @Test
    void whenLogged_getDictionaries_shouldReturnOkStatus() throws Exception {
        int pageSize = 2;
        Mockito.when(dictionaryService.getDictionaries(0, pageSize, "filter"))
                .thenReturn(new DictionariesDtoResponse(new ArrayList<>(), 1));
        String token = mockedLogin(standardUser);
        MvcResult result = DictionaryControllerUtils.getDictionaries(mockMvc, token, 0, pageSize, "filter");
        Mockito.verify(dictionaryService, Mockito.times(1))
                .getDictionaries(0, pageSize, "filter");
        Assertions.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenLogged_getDictionaryWordsForUser_shouldReturnOkStatus() throws Exception {
        String token = mockedLogin(standardUser);
        Mockito.when(wordService.getDictionaryWordsForUser(1, 0, properties.getMaxPageSize(), "filter"))
                .thenReturn(new GetDictionaryWordsDtoResponse(
                        new HashSet<>(), 1
                ));
        MvcResult result = DictionaryControllerUtils
                .getDictionaryWordsForUser(mockMvc, token, 1, 0, properties.getMaxPageSize(), "filter");
        Mockito.verify(wordService, Mockito.times(1))
                .getDictionaryWordsForUser(1, 0, properties.getMaxPageSize(), "filter");
        Assertions.assertEquals(200, result.getResponse().getStatus());
    }
}
