package net.thumbtack.spaced.repetition.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.model.enums.RoleName;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {
    private int id;
    private String username;
    private String email;
    private Set<RoleName> roles;
}
