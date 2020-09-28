package net.thumbtack.spaced.repetition.commons;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class AdminControllerUtils {
    public static final String AUTHORIZATION = "Authorization";

    public static MvcResult getAllUsers(MockMvc mockMvc, String token, int page, int pageSize, String filter) throws Exception {
        return mockMvc.perform(
                get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("pageSize", String.valueOf(pageSize))
                        .queryParam("filter", filter)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult getAllWords(MockMvc mockMvc, String token, int page, int pageSize, String filter) throws Exception {
        return mockMvc.perform(
                get("/api/admin/words")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("pageSize", String.valueOf(pageSize))
                        .queryParam("filter", filter)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult addWord(MockMvc mockMvc, String token, String request) throws Exception {
        return mockMvc.perform(
                post("/api/admin/words")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
                        .content(request))
                .andReturn();
    }

    public static MvcResult addWord(MockMvc mockMvc, String token, String request, int dictionaryId) throws Exception {
        return mockMvc.perform(
                post("/api/admin/words")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("dictionaryId", String.valueOf(dictionaryId))
                        .header(AUTHORIZATION, token)
                        .content(request))
                .andReturn();
    }

    public static MvcResult editWord(MockMvc mockMvc, String token, String request, int wordId) throws Exception {
        return mockMvc.perform(
                put(String.format("/api/admin/words/%d", wordId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
                        .content(request))
                .andReturn();
    }

    public static MvcResult deleteWord(MockMvc mockMvc, String token, int wordId) throws Exception {
        return mockMvc.perform(
                delete(String.format("/api/admin/words/%d", wordId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult addDictionary(MockMvc mockMvc, String token, String request) throws Exception {
        return mockMvc.perform(
                post("/api/admin/dictionaries")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
                        .content(request))
                .andReturn();
    }

    public static MvcResult addWordToDictionary(MockMvc mockMvc, String token,
                                                int dictionaryId, int wordId) throws Exception {
        return mockMvc.perform(
                put(String.format("/api/admin/dictionaries/%d/words/%d", dictionaryId, wordId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
        ).andReturn();
    }

    public static MvcResult deleteWordFromDictionary(MockMvc mockMvc, String token,
                                                     int dictionaryId, int wordId) throws Exception {
        return mockMvc.perform(
                delete(String.format("/api/admin/dictionaries/%d/words/%d", dictionaryId, wordId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult deleteUser(MockMvc mockMvc, String token, int userId) throws Exception {
        return mockMvc.perform(
                delete(String.format("/api/admin/users/%d", userId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult deleteDictionary(MockMvc mockMvc, String token, int dictionaryId) throws Exception {
        return mockMvc.perform(
                delete(String.format("/api/admin/dictionaries/%d", dictionaryId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token))
                .andReturn();
    }

    public static MvcResult uploadWordsFromFile(MockMvc mockMvc, String token,
                                                String fileData, int dictionaryId) throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "file.csv",
                        MediaType.TEXT_PLAIN_VALUE,
                        fileData.getBytes(StandardCharsets.UTF_8)
                );


        return mockMvc.perform(
                multipart("/api/admin/words/upload")
                        .file(file)
                        .queryParam("dictionaryId", String.valueOf(dictionaryId))
                        .header(AUTHORIZATION, token)
        ).andReturn();
    }

    public static MvcResult renameDictionary(MockMvc mockMvc, String token, String request, int dictionaryId) throws Exception {
        return mockMvc.perform(
                put(String.format("/api/admin/dictionaries/%d", dictionaryId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
                        .content(request))
                .andReturn();
    }

}
