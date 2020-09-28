package net.thumbtack.spaced.repetition.dto.request.user;

import net.thumbtack.spaced.repetition.dto.validation.PasswordSize;

public class ChangePasswordRequest {
    private int id;
    @PasswordSize
    private String oldPassword;
    @PasswordSize
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(int id, String oldPassword, String newPassword) {
        this.id = id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
