package net.thumbtack.spaced.repetition.dto.response.dictionary;

import java.util.List;

public class DictionariesDtoResponse {
    private List<DictionaryDtoResponse> dictionaries;
    private int totalPages;

    public DictionariesDtoResponse() {
    }

    public DictionariesDtoResponse(List<DictionaryDtoResponse> dictionaries, int totalPages) {
        this.dictionaries = dictionaries;
        this.totalPages = totalPages;
    }

    public List<DictionaryDtoResponse> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(List<DictionaryDtoResponse> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
