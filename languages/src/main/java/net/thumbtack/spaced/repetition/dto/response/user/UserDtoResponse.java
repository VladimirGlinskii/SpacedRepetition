package net.thumbtack.spaced.repetition.dto.response.user;

import net.thumbtack.spaced.repetition.model.enums.RoleName;

import java.util.Objects;
import java.util.Set;

public class UserDtoResponse {

    private int id;

    private String username;

    private String email;

    private Set<RoleName> roles;

    public UserDtoResponse() {
    }

    public UserDtoResponse(int id, String username, String email, Set<RoleName> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleName> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleName> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDtoResponse)) return false;
        UserDtoResponse response = (UserDtoResponse) o;
        return getId() == response.getId() &&
                Objects.equals(getUsername(), response.getUsername()) &&
                Objects.equals(getEmail(), response.getEmail()) &&
                Objects.equals(getRoles(), response.getRoles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getEmail(), getRoles());
    }
}
