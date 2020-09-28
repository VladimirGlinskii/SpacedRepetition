package net.thumbtack.spaced.repetition.dto.response.word;

import java.util.Set;

public class GetDictionaryWordsDtoResponse {
    private Set<WordDtoResponse> words;
    private int totalPages;

    public GetDictionaryWordsDtoResponse() {
    }

    public GetDictionaryWordsDtoResponse(Set<WordDtoResponse> words, int totalPages) {
        this.words = words;
        this.totalPages = totalPages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public Set<WordDtoResponse> getWords() {
        return words;
    }

    public void setWords(Set<WordDtoResponse> words) {
        this.words = words;
    }
}
