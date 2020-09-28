package net.thumbtack.spaced.repetition.commons;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class DictionaryControllerUtils {

    public static final String AUTHORIZATION = "Authorization";

    public static MvcResult getDictionaries(MockMvc mockMvc, String token, int page, int pageSize, String filter) throws Exception {
        return mockMvc
                .perform(
                        get("/api/dictionaries")
                                .header(AUTHORIZATION, token)
                                .queryParam("page", String.valueOf(page))
                                .queryParam("pageSize", String.valueOf(pageSize))
                                .queryParam("filter", filter)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andReturn();
    }

    public static MvcResult getDictionaryWordsForUser(MockMvc mockMvc, String token, int id,
                                                      int page, int pageSize, String filter) throws Exception {
        return mockMvc
                .perform(
                        get(String.format("/api/dictionaries/%d", id))
                                .header(AUTHORIZATION, token)
                                .queryParam("page", String.valueOf(page))
                                .queryParam("pageSize", String.valueOf(pageSize))
                                .queryParam("filter", filter)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andReturn();
    }


}
