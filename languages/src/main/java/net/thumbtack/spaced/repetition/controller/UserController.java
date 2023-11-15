package net.thumbtack.spaced.repetition.controller;

import jakarta.validation.Valid;
import net.thumbtack.spaced.repetition.dto.request.user.ChangePasswordRequest;
import net.thumbtack.spaced.repetition.dto.request.user.LoginDtoRequest;
import net.thumbtack.spaced.repetition.dto.request.user.RegisterDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.statistics.WeeklyStatisticsResponse;
import net.thumbtack.spaced.repetition.dto.response.user.LoginDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.RegisterDtoResponse;
import net.thumbtack.spaced.repetition.model.Role;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.security.jwt.JwtTokenService;
import net.thumbtack.spaced.repetition.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("registration")
    public ResponseEntity<RegisterDtoResponse> register(
            @RequestBody @Valid RegisterDtoRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PutMapping("login")
    public ResponseEntity<LoginDtoResponse> login(
            @RequestBody LoginDtoRequest request) {
        try {
            String username = request.getUsername();
            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(username,
                                    request.getPassword()));
            User user = userService.findByUsername(username);

            String token = jwtTokenService.createToken(user);

            LoginDtoResponse response =
                    new LoginDtoResponse(user.getId(), user.getEmail(), token,
                            user.getRoles()
                                    .stream()
                                    .map(Role::getName)
                                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("users/words/{id}")
    public ResponseEntity<Void> addUsersWord(@PathVariable int id) {
        userService.addUsersWord(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("users/words/{id}")
    public ResponseEntity<Void> deleteUsersWord(@PathVariable int id) {
        userService.deleteUsersWord(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("users/dictionaries/{dictionaryId}")
    public ResponseEntity<Void> selectWholeDictionary(@PathVariable int dictionaryId) {
        userService.selectWholeDictionary(dictionaryId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("users/dictionaries/{dictionaryId}")
    public ResponseEntity<Void> unselectWholeDictionary(@PathVariable int dictionaryId) {
        userService.unselectWholeDictionary(dictionaryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("users/statistics")
    public ResponseEntity<WeeklyStatisticsResponse> getWeeklyStatistics() {
        return ResponseEntity.ok(userService.getWeeklyStatistics());
    }

    @PutMapping("users")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

}
