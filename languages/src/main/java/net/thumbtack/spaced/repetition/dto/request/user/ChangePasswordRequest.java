package net.thumbtack.spaced.repetition.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.dto.validation.PasswordSize;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private int id;

    @PasswordSize
    private String oldPassword;

    @PasswordSize
    private String newPassword;
}
