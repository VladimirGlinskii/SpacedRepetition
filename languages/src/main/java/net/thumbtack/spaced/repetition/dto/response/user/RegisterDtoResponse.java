package net.thumbtack.spaced.repetition.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDtoResponse {
    private int id;
    private String username;
}
