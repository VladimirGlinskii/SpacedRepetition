package net.thumbtack.spaced.repetition.dto.response.user;

public class RegisterDtoResponse {
    private int id;
    private String username;

    public RegisterDtoResponse() {
    }

    public RegisterDtoResponse(int id, String username) {
        this.id = id;
        this.username = username;
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
}
