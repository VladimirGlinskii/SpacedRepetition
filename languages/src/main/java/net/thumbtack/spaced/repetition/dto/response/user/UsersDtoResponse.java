package net.thumbtack.spaced.repetition.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDtoResponse {
    private List<UserDtoResponse> users;
    private int totalPages;
}
