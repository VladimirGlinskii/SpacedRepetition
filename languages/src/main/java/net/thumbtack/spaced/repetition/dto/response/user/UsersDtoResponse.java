package net.thumbtack.spaced.repetition.dto.response.user;

import java.util.List;
import java.util.Objects;

public class UsersDtoResponse {
    private List<UserDtoResponse> users;
    private int totalPages;

    public UsersDtoResponse() {
    }

    public UsersDtoResponse(List<UserDtoResponse> users, int totalPages) {
        this.users = users;
        this.totalPages = totalPages;
    }

    public List<UserDtoResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserDtoResponse> users) {
        this.users = users;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersDtoResponse)) return false;
        UsersDtoResponse that = (UsersDtoResponse) o;
        return Objects.equals(getUsers(), that.getUsers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsers());
    }
}
