package net.thumbtack.spaced.repetition.mock.service;

import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.request.user.ChangePasswordRequest;
import net.thumbtack.spaced.repetition.dto.request.user.RegisterDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.statistics.StatisticsResponse;
import net.thumbtack.spaced.repetition.dto.response.statistics.WeeklyStatisticsResponse;
import net.thumbtack.spaced.repetition.dto.response.user.RegisterDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.UserDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.UsersDtoResponse;
import net.thumbtack.spaced.repetition.exception.ErrorCode;
import net.thumbtack.spaced.repetition.exception.ServerRuntimeException;
import net.thumbtack.spaced.repetition.model.*;
import net.thumbtack.spaced.repetition.model.enums.RoleName;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import net.thumbtack.spaced.repetition.repos.StatisticsRepository;
import net.thumbtack.spaced.repetition.repos.UserRepository;
import net.thumbtack.spaced.repetition.repos.UsersWordRepository;
import net.thumbtack.spaced.repetition.repos.WordRepository;
import net.thumbtack.spaced.repetition.service.UserService;
import net.thumbtack.spaced.repetition.utils.OffsetBasedPageable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceMockTest extends ServiceMockTestBase {

    @Spy
    private final DelegatingPasswordEncoder delegatingPasswordEncoder = getDelegatingPasswordEncoder();
    @Spy
    private final ApplicationProperties properties = getProperties();
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UsersWordRepository usersWordRepository;
    @Mock
    private StatisticsRepository statisticsRepository;
    @Mock
    private WordRepository wordRepository;

    @BeforeEach
    public void init() {
        super.init();

    }

    @Test
    void register_shouldSaveEncodedPasswordAndReturnCorrectResponse() {
        RegisterDtoRequest request = new RegisterDtoRequest(
                standardUser.getUsername(),
                standardUser.getEmail(),
                standardUser.getPassword()
        );

        RegisterDtoResponse response = userService.register(request);
        Assertions.assertEquals(request.getUsername(), response.getUsername());
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(capturedUser.capture());
        Assertions.assertEquals(request.getUsername(), capturedUser.getValue().getUsername());
        Assertions.assertEquals(request.getEmail(), capturedUser.getValue().getEmail());
        Assertions.assertTrue(delegatingPasswordEncoder.matches(request.getPassword(), capturedUser.getValue().getPassword()));
        Assertions.assertEquals(1, capturedUser.getValue().getRoles().size());
        Assertions.assertTrue(capturedUser.getValue().getRoles().contains(new Role(1, RoleName.ROLE_USER)));
    }

    @Test
    void whenUsernameExists_register_shouldThrowException() {
        RegisterDtoRequest request = new RegisterDtoRequest(
                standardUser.getUsername(),
                standardUser.getEmail(),
                standardUser.getPassword()
        );
        Mockito.when(userRepository
                .findByUsernameOrEmailIgnoreCase(standardUser.getUsername(), standardUser.getEmail()))
                .thenReturn(new User(standardUser.getUsername(), "email", "password"));
        try {
            userService.register(request);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.USERNAME_ALREADY_EXISTS, e.getErrorCode());
        }
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void whenEmailExists_register_shouldThrowException() {
        RegisterDtoRequest request = new RegisterDtoRequest(
                standardUser.getUsername(),
                standardUser.getEmail(),
                standardUser.getPassword()
        );
        Mockito.when(userRepository
                .findByUsernameOrEmailIgnoreCase(standardUser.getUsername(), standardUser.getEmail()))
                .thenReturn(new User("username", standardUser.getEmail(), "password"));
        try {
            userService.register(request);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, e.getErrorCode());
        }
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void whenPage0_getAll_shouldReturnCorrectResponse() {
        int pageSize = 3;

        List<User> users = Arrays.asList(
                standardUser,
                admin,
                new User(100, "user1", "email1", "password", standardUser.getRoles()),
                new User(101, "user2", "email2", "password", standardUser.getRoles()),
                new User(102, "user3", "email3", "password", standardUser.getRoles())
        );
        Pageable pageable = new OffsetBasedPageable(
                0,
                pageSize,
                Sort.Direction.ASC, "id");
        Mockito.when(userRepository.findAllByUsernameStartsWithIgnoreCase(pageable, "filter"))
                .thenReturn(new PageImpl<>(users.subList(0, pageSize),
                        pageable, users.size()));
        UsersDtoResponse response = userService.getAll(0, pageSize, "filter");

        Assertions.assertEquals(pageSize, response.getUsers().size());
        Assertions.assertEquals(
                users.stream().limit(pageSize).map(
                        u -> new UserDtoResponse(
                                u.getId(), u.getUsername(), u.getEmail(),
                                u.getRoles().stream().map(Role::getName)
                                        .collect(Collectors.toSet())))
                        .collect(Collectors.toList()),
                response.getUsers()
        );
        Assertions.assertEquals(2, response.getTotalPages());
    }

    @Test
    void whenPageSizeTooBig_getAll_shouldSetMaxPageSize() {
        List<User> users = Arrays.asList(
                standardUser,
                admin
        );
        int pageSize = properties.getMaxPageSize() + 1;
        Pageable pageable = new OffsetBasedPageable(
                0,
                properties.getMaxPageSize(),
                Sort.Direction.ASC, "id");
        Mockito.when(userRepository.findAllByUsernameStartsWithIgnoreCase(pageable, ""))
                .thenReturn(new PageImpl<>(users,
                        pageable, users.size()));
        userService.getAll(0, pageSize, "");
        Mockito.verify(userRepository, Mockito.times(1))
                .findAllByUsernameStartsWithIgnoreCase(pageable, "");
    }

    @Test
    void whenPageSizeIsNegative_getAll_shouldSetMaxPageSize() {
        List<User> users = Arrays.asList(
                standardUser,
                admin
        );
        int pageSize = -1;
        Pageable pageable = new OffsetBasedPageable(
                0,
                properties.getMaxPageSize(),
                Sort.Direction.ASC, "id");
        Mockito.when(userRepository.findAllByUsernameStartsWithIgnoreCase(pageable, ""))
                .thenReturn(new PageImpl<>(users,
                        pageable, users.size()));
        userService.getAll(0, pageSize, "");
        Mockito.verify(userRepository, Mockito.times(1))
                .findAllByUsernameStartsWithIgnoreCase(pageable, "");
    }

    @Test
    void getUserById_shouldReturnCorrectResponse() {
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(standardUser));
        UserDtoResponse response = userService.getUserById(1);
        Assertions.assertEquals(1, response.getId());
        Assertions.assertEquals(standardUser.getUsername(), response.getUsername());
        Assertions.assertEquals(standardUser.getEmail(), response.getEmail());
        Assertions.assertEquals(Collections.singleton(RoleName.ROLE_USER), response.getRoles());
    }

    @Test
    void getUserById_shouldThrowException() {
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.empty());
        try {
            userService.getUserById(1);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.USER_ID_NOT_EXISTS, e.getErrorCode());
        }
    }

    @Test
    void whenUsersWordExists_addUsersWord_shouldChangeStatusToActive() {
        mockSecurityContextHolderForUser(standardUser);
        UsersWord usersWord = new UsersWord(1, standardUser, words[0],
                0, UsersWordStatus.ACTIVE, LocalDateTime.now());
        Mockito.when(usersWordRepository
                .findByUserIdAndWordId(standardUser.getId(), words[0].getId()))
                .thenReturn(
                        new UsersWord(usersWord.getId(), usersWord.getUser(),
                                usersWord.getWord(), usersWord.getLastInterval(),
                                UsersWordStatus.DISABLED, usersWord.getNextDatetime())
                );
        userService.addUsersWord(words[0].getId());

        Mockito.verify(usersWordRepository, Mockito.times(1))
                .save(usersWord);
    }

    @Test
    void whenUsersWordNotExists_addUsersWord_shouldCreateUsersWord() {
        mockSecurityContextHolderForUser(standardUser);
        UsersWord usersWord = new UsersWord(null, standardUser, words[0],
                0, UsersWordStatus.ACTIVE, LocalDateTime.now());
        Mockito.when(usersWordRepository
                .findByUserIdAndWordId(standardUser.getId(), words[0].getId()))
                .thenReturn(null);
        Mockito.when(wordRepository.findById(words[0].getId()))
                .thenReturn(Optional.of(words[0]));
        userService.addUsersWord(words[0].getId());

        Mockito.verify(wordRepository, Mockito.times(1)).findById(words[0].getId());
        Mockito.verify(usersWordRepository, Mockito.times(1))
                .save(usersWord);
    }

    @Test
    void whenWordNotExists_addUsersWord_shouldThrowException() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(usersWordRepository
                .findByUserIdAndWordId(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(null);
        Mockito.when(wordRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        try {
            userService.addUsersWord(words[0].getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WORD_NOT_FOUND, e.getErrorCode());
        }

        Mockito.verify(wordRepository, Mockito.times(1)).findById(words[0].getId());
        Mockito.verify(usersWordRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void whenUsersWordExists_deleteUsersWord_shouldSaveDisableStatus() {
        mockSecurityContextHolderForUser(standardUser);
        UsersWord usersWord = new UsersWord(1, standardUser, words[0],
                0, UsersWordStatus.DISABLED, LocalDateTime.now());
        Mockito.when(usersWordRepository
                .findByUserIdAndWordId(standardUser.getId(), words[0].getId()))
                .thenReturn(
                        new UsersWord(usersWord.getId(), usersWord.getUser(),
                                usersWord.getWord(), usersWord.getLastInterval(),
                                UsersWordStatus.ACTIVE, usersWord.getNextDatetime())
                );
        userService.deleteUsersWord(words[0].getId());

        Mockito.verify(usersWordRepository, Mockito.times(1))
                .save(usersWord);
    }

    @Test
    void whenUsersWordNotExists_deleteUsersWord_shouldDoNothing() {
        mockSecurityContextHolderForUser(standardUser);
        Mockito.when(usersWordRepository
                .findByUserIdAndWordId(standardUser.getId(), words[0].getId()))
                .thenReturn(null);
        userService.deleteUsersWord(words[0].getId());

        Mockito.verify(usersWordRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void getWeeklyStatistics_shouldReturnStatisticsForAllWeek() {
        mockSecurityContextHolderForUser(standardUser);
        Statistics stat1 = new Statistics(1, standardUser, 1, 1,
                LocalDate.now().minusDays(5));
        Statistics stat2 = new Statistics(2, standardUser, 2, 2,
                LocalDate.now().minusDays(1));
        Mockito.when(statisticsRepository
                .findAllByUserIdAndDateGreaterThanEqualOrderByDateAsc(standardUser.getId(), LocalDate.now().minusDays(6)))
                .thenReturn(Arrays.asList(stat1, stat2));
        WeeklyStatisticsResponse response = userService.getWeeklyStatistics();
        for (int i = 0; i < 7; i++) {
            if (i != 1 && i != 5) {
                Assertions.assertEquals(
                        response.getWeeklyStatistics().get(i),
                        new StatisticsResponse(standardUser.getUsername(), LocalDate.now().minusDays(6 - i)));
            }
        }
        Assertions.assertEquals(
                response.getWeeklyStatistics().get(1),
                new StatisticsResponse(
                        standardUser.getUsername(),
                        stat1.getCorrectAnswers(),
                        stat1.getWrongAnswers(),
                        stat1.getDate())
        );
        Assertions.assertEquals(
                response.getWeeklyStatistics().get(5),
                new StatisticsResponse(
                        standardUser.getUsername(),
                        stat2.getCorrectAnswers(),
                        stat2.getWrongAnswers(),
                        stat2.getDate())
        );
    }

    @Test
    void whenUserNotExists_deleteUser_shouldThrowException() {
        Mockito.when(userRepository.findById(standardUser.getId()))
                .thenThrow(new ServerRuntimeException(ErrorCode.USER_ID_NOT_EXISTS));
        try {
            userService.deleteUser(standardUser.getId());
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.USER_ID_NOT_EXISTS, e.getErrorCode());
        }
    }

    @Test
    void whenUserExists_deleteUser_shouldDeleteUser() {
        Mockito.when(userRepository.findById(standardUser.getId()))
                .thenReturn(Optional.of(standardUser));
        userService.deleteUser(standardUser.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .delete(standardUser);
    }

    @Test
    void whenUserNotExists_changePassword_shouldThrowException() {
        Mockito.when(userRepository.findById(standardUser.getId()))
                .thenThrow(new ServerRuntimeException(ErrorCode.USER_ID_NOT_EXISTS));
        ChangePasswordRequest request =
                new ChangePasswordRequest(standardUser.getId(), "oldPassword", "newPassword");
        try {
            userService.changePassword(request);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.USER_ID_NOT_EXISTS, e.getErrorCode());
        }
    }

    @Test
    void whenOldPasswordIsWrong_changePassword_shouldThrowException() {
        DelegatingPasswordEncoder encoder = getDelegatingPasswordEncoder();
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(
                        new User(1, "username", "email@gmail.com",
                                encoder.encode("oldPassword"))));
        ChangePasswordRequest request =
                new ChangePasswordRequest(standardUser.getId(), "wrongPassword", "newPassword");
        try {
            userService.changePassword(request);
            fail();
        } catch (ServerRuntimeException e) {
            Assertions.assertEquals(ErrorCode.WRONG_PASSWORD, e.getErrorCode());
        }
    }

    @Test
    void whenOldPasswordIsCorrect_changePassword_shouldSaveNewPasswordEncoded() {
        DelegatingPasswordEncoder encoder = getDelegatingPasswordEncoder();
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(
                        new User(1, "username", "email@gmail.com",
                                encoder.encode("oldPassword"))));
        ChangePasswordRequest request =
                new ChangePasswordRequest(standardUser.getId(), "oldPassword", "newPassword");
        userService.changePassword(request);
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1))
                .save(savedUser.capture());
        Assertions.assertTrue(
                encoder.matches("newPassword", savedUser.getValue().getPassword())
        );
    }

    @Test
    void selectWholeDictionary_shouldSetActiveStatusForAllDictionaryWords() {
        mockSecurityContextHolderForUser(standardUser);

        Mockito.when(usersWordRepository.getDictionaryUntrackedWords(1, standardUser.getId()))
                .thenReturn(Arrays.asList(
                        new Word(1, "word1", "[\"transl\"]"),
                        new Word(2, "word2", "[\"transl\"]"),
                        new Word(3, "word3", "[\"transl\"]")));

        ArgumentCaptor<List<UsersWord>> savedWords = ArgumentCaptor.forClass(List.class);
        userService.selectWholeDictionary(1);

        Mockito.verify(usersWordRepository, Mockito.times(1))
                .selectUnselectedDictionaryWords(1, standardUser.getId());
        Mockito.verify(usersWordRepository, Mockito.times(1))
                .saveAll(savedWords.capture());
        Assertions.assertEquals(3, savedWords.getValue().size());
        savedWords.getValue().forEach(uw -> Assertions.assertEquals(UsersWordStatus.ACTIVE, uw.getStatus()));
    }

    @Test
    void unselectWholeDictionary_shouldUnselectSelectedDictionaryWords() {
        mockSecurityContextHolderForUser(standardUser);

        userService.unselectWholeDictionary(1);

        Mockito.verify(usersWordRepository, Mockito.times(1))
                .unselectSelectedDictionaryWords(1, standardUser.getId());
    }
}
