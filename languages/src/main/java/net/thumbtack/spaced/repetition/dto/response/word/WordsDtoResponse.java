package net.thumbtack.spaced.repetition.dto.response.word;

import java.util.List;

public class WordsDtoResponse {
    private List<WordDtoResponse> words;
    private int totalPages;

    public WordsDtoResponse() {
    }

    public WordsDtoResponse(List<WordDtoResponse> words, int totalPages) {
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
