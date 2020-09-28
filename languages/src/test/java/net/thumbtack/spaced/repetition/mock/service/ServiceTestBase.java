package net.thumbtack.spaced.repetition.mock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.model.Role;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.model.UsersWord;
import net.thumbtack.spaced.repetition.model.Word;
import net.thumbtack.spaced.repetition.model.enums.RoleName;
import net.thumbtack.spaced.repetition.model.enums.UsersWordStatus;
import net.thumbtack.spaced.repetition.repos.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class ServiceTestBase {

    protected User standardUser =
            new User(
                    1, "Username",
                    "username@gmail.com", "password",
                    Collections.singleton(new Role(1, RoleName.ROLE_USER))
            );

    protected User admin =
            new User(
                    2, "admin",
                    "admin@gmail.com", "admin",
                    new HashSet<Role>() {{
                        add(new Role(1, RoleName.ROLE_USER));
                        add(new Role(2, RoleName.ROLE_ADMIN));
                    }}
            );

    protected Word[] words = {
            new Word(1, "word1", "[\"transl1\"]"),
            new Word(2, "word2", "[\"transl2\"]"),
            new Word(3, "word3", "[\"transl3\"]"),
            new Word(4, "word4", "[\"transl4\"]"),
            new Word(5, "word5", "[\"transl5\"]"),
            new Word(6, "word6", "[\"transl6\"]"),
    };

    protected UsersWord[] standardUsersWords;

    @MockBean
    protected DictionaryRepository dictionaryRepository;

    @MockBean
    protected ExerciseRepository exerciseRepository;

    @MockBean
    protected RoleRepository roleRepository;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected UsersWordRepository usersWordRepository;

    @MockBean
    protected WordRepository wordRepository;

    @MockBean
    protected StatisticsRepository statisticsRepository;

    @Autowired
    protected ApplicationProperties properties;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void clear() {
        Mockito.reset(dictionaryRepository, exerciseRepository, roleRepository,
                userRepository, usersWordRepository, wordRepository, statisticsRepository);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        standardUsersWords = new UsersWord[]{
                new UsersWord(1, standardUser, words[0], 0,
                        UsersWordStatus.ACTIVE, LocalDateTime.now()),
                new UsersWord(2, standardUser, words[1], 0,
                        UsersWordStatus.DISABLED, LocalDateTime.now()),
                new UsersWord(3, standardUser, words[2], 0,
                        UsersWordStatus.ACTIVE, LocalDateTime.now())
        };

    }

    protected void mockSecurityContextHolderForUser(User user) {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
    }

    protected String getSimpleTranslation(Word word) {
        return word.getTranslation().replaceAll("[\\[\\]\"]", "");
    }

    protected Set<String> getTranslationAsSet(Word word) {
        try {
            return objectMapper.readValue(word.getTranslation(), new TypeReference<Set<String>>() {
            });
        } catch (JsonProcessingException e) {
            return new HashSet<>();
        }
    }
}
