package net.thumbtack.spaced.repetition.dto.response.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDictionaryWordsDtoResponse {
    private Set<WordDtoResponse> words;
    private int totalPages;
}
