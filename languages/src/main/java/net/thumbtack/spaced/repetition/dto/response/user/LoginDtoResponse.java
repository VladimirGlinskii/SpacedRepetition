package net.thumbtack.spaced.repetition.dto.response.user;

import net.thumbtack.spaced.repetition.model.enums.RoleName;

import java.util.List;

public class LoginDtoResponse {
    private int id;
    private String email;
    private String token;
    private List<RoleName> roles;

    public LoginDtoResponse() {
    }

    public LoginDtoResponse(int id, String email, String token, List<RoleName> roles) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<RoleName> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleName> roles) {
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
