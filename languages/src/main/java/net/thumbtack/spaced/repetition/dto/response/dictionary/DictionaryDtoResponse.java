package net.thumbtack.spaced.repetition.dto.response.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryDtoResponse {
    private int id;
    private String name;
}
