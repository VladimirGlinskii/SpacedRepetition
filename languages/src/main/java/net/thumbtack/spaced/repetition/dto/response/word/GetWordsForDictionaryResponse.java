package net.thumbtack.spaced.repetition.dto.response.word;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize
@JsonDeserialize
public class GetWordsForDictionaryResponse {
    private List<WordDtoResponse> words;
    private int totalPages;

    public GetWordsForDictionaryResponse() {
    }

    public GetWordsForDictionaryResponse(List<WordDtoResponse> words, int totalPages) {
        this.words = words;
        this.totalPages = totalPages;
    }

    public List<WordDtoResponse> getWords() {
        return words;
    }

    public void setWords(List<WordDtoResponse> words) {
        this.words = words;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
