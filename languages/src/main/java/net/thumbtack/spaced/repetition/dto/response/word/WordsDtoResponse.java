package net.thumbtack.spaced.repetition.dto.response.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordsDtoResponse {
    private List<WordDtoResponse> words;
    private int totalPages;
}
