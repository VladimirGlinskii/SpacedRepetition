package net.thumbtack.spaced.repetition.service;

import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.dto.request.user.ChangePasswordRequest;
import net.thumbtack.spaced.repetition.dto.request.user.RegisterDtoRequest;
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
import net.thumbtack.spaced.repetition.utils.OffsetBasedPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service(value = "userService")
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private DelegatingPasswordEncoder delegatingPasswordEncoder;
    private UsersWordRepository usersWordRepository;
    private StatisticsRepository statisticsRepository;
    private WordRepository wordRepository;
    private ApplicationProperties properties;

    public UserService(UserRepository userRepository,
                       DelegatingPasswordEncoder delegatingPasswordEncoder,
                       UsersWordRepository usersWordRepository,
                       StatisticsRepository statisticsRepository,
                       WordRepository wordRepository,
                       ApplicationProperties properties) {
        this.userRepository = userRepository;
        this.delegatingPasswordEncoder = delegatingPasswordEncoder;
        this.usersWordRepository = usersWordRepository;
        this.statisticsRepository = statisticsRepository;
        this.wordRepository = wordRepository;
        this.properties = properties;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOutdatedStatistics() {
        statisticsRepository.deleteStatisticsByDateBefore(LocalDate.now().minusDays(6));
    }

    public RegisterDtoResponse register(RegisterDtoRequest request) throws ServerRuntimeException {
        User userFromDB = userRepository.findByUsernameOrEmailIgnoreCase(request.getUsername(), request.getEmail());
        if (userFromDB != null) {
            if (userFromDB.getUsername().equals(request.getUsername()))
                throw new ServerRuntimeException(ErrorCode.USERNAME_ALREADY_EXISTS);
            else
                throw new ServerRuntimeException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());
        user.setRoles(Collections.singleton(new Role(1, RoleName.ROLE_USER)));
        user.setPassword(delegatingPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new RegisterDtoResponse(user.getId(), user.getUsername());
    }

    public UsersDtoResponse getAll(int page, int pageSize, String filter) {
        if (pageSize > properties.getMaxPageSize() || pageSize <= 0) {
            pageSize = properties.getMaxPageSize();
        }
        Pageable pageable = new OffsetBasedPageable(
                ((long) page) * pageSize,
                pageSize,
                Sort.Direction.ASC, "id");
        Page<User> users = userRepository.findAllByUsernameStartsWithIgnoreCase(pageable, filter);

        return new UsersDtoResponse(
                users.stream().map(
                        u -> new UserDtoResponse(
                                u.getId(),
                                u.getUsername(),
                                u.getEmail(),
                                u.getRoles().stream().map(Role::getName)
                                        .collect(Collectors.toSet())))
                        .collect(Collectors.toList()), users.getTotalPages()
        );
    }

    public UserDtoResponse getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.USER_ID_NOT_EXISTS));
        Set<RoleName> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserDtoResponse(user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username);

        if (user == null) {
            throw new ServerRuntimeException(ErrorCode.USERNAME_NOT_FOUND);
        }
        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public void addUsersWord(int wordId) {
        User user = ServiceUtils.getUser();
        UsersWord usersWord = usersWordRepository.findByUserIdAndWordId(user.getId(), wordId);
        if (usersWord == null) {
            Word word = wordRepository
                    .findById(wordId)
                    .orElseThrow(() -> new ServerRuntimeException(ErrorCode.WORD_NOT_FOUND));
            usersWord = new UsersWord(user, word);
        } else {
            usersWord.setStatus(UsersWordStatus.ACTIVE);
        }

        usersWordRepository.save(usersWord);
    }

    public void deleteUsersWord(int wordId) {
        User user = ServiceUtils.getUser();
        UsersWord usersWord = usersWordRepository.findByUserIdAndWordId(user.getId(), wordId);
        if (usersWord != null) {
            usersWord.setStatus(UsersWordStatus.DISABLED);
            usersWordRepository.save(usersWord);
        }
    }

    @Transactional
    public void selectWholeDictionary(int dictionaryId) {
        User user = ServiceUtils.getUser();
        List<Word> words = usersWordRepository.getDictionaryUntrackedWords(dictionaryId, user.getId());
        List<UsersWord> usersWords = words.stream()
                .map(w -> new UsersWord(user, w)).collect(Collectors.toList());

        usersWordRepository.selectUnselectedDictionaryWords(dictionaryId, user.getId());
        usersWordRepository.saveAll(usersWords);
    }

    @Modifying
    @Transactional
    public void unselectWholeDictionary(int dictionaryId) {
        User user = ServiceUtils.getUser();
        usersWordRepository.unselectSelectedDictionaryWords(dictionaryId, user.getId());
    }

    public WeeklyStatisticsResponse getWeeklyStatistics() {
        User user = ServiceUtils.getUser();
        List<Statistics> statisticsList = statisticsRepository
                .findAllByUserIdAndDateGreaterThanEqualOrderByDateAsc(user.getId(), LocalDate.now().minusDays(6));

        return new WeeklyStatisticsResponse(statisticsList, user);
    }

    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.USER_ID_NOT_EXISTS));
        userRepository.delete(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ServerRuntimeException(ErrorCode.USER_ID_NOT_EXISTS));
        if (!delegatingPasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ServerRuntimeException(ErrorCode.WRONG_PASSWORD);
        }
        user.setPassword(delegatingPasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
