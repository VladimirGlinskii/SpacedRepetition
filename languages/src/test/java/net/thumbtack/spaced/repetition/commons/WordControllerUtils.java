package net.thumbtack.spaced.repetition.commons;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class WordControllerUtils {

    public static final String AUTHORIZATION = "Authorization";

    public static MvcResult getWordsForDictionary(
            MockMvc mockMvc, String token, int dictionaryId, int page, int pageSize,
            boolean showAll, String filter) throws Exception {
        return mockMvc
                .perform(
                        get(String.format("/api/words/dictionary/%d", dictionaryId))
                                .header(AUTHORIZATION, token)
                                .queryParam("page", String.valueOf(page))
                                .queryParam("pageSize", String.valueOf(pageSize))
                                .queryParam("showAll", String.valueOf(showAll))
                                .queryParam("filter", filter)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andReturn();
    }

    public static MvcResult getWordPronunciationLink(
            MockMvc mockMvc, String token, String word) throws Exception {
        return mockMvc
                .perform(
                        get(String.format("/api/words/%s/pronunciation", word))
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andReturn();
    }

}
