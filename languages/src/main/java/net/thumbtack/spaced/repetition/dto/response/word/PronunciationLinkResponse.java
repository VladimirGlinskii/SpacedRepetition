package net.thumbtack.spaced.repetition.dto.response.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PronunciationLinkResponse {
    private String url;
}
