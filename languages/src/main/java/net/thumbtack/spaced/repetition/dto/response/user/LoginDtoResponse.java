package net.thumbtack.spaced.repetition.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.model.enums.RoleName;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDtoResponse {
    private int id;
    private String email;
    private String token;
    private List<RoleName> roles;
}
