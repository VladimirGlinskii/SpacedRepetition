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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceMockTestBase {
    private final ObjectMapper objectMapper = new ObjectMapper();
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

    @BeforeEach
    public void init() {
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

    @AfterAll
    static void reset(){
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
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

    protected ApplicationProperties getProperties() {
        return new ApplicationProperties("secret",
                1L, 10,
                10, 5,
                50, 20);
    }

    protected DelegatingPasswordEncoder getDelegatingPasswordEncoder() {
        PasswordEncoder defaultEncoder = new LdapShaPasswordEncoder();
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("ldap", defaultEncoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(
                "ldap", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(defaultEncoder);

        return passwordEncoder;
    }
}
