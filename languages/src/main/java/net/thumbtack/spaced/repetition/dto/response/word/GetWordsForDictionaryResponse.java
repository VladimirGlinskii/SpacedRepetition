package net.thumbtack.spaced.repetition.dto.response.word;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonSerialize
@JsonDeserialize
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWordsForDictionaryResponse {
    private List<WordDtoResponse> words;
    private int totalPages;
}
