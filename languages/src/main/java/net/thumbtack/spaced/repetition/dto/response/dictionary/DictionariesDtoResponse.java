package net.thumbtack.spaced.repetition.dto.response.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionariesDtoResponse {
    private List<DictionaryDtoResponse> dictionaries;
    private int totalPages;
}
