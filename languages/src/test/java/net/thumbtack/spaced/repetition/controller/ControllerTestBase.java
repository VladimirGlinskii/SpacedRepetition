package net.thumbtack.spaced.repetition.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.repos.RoleRepository;
import net.thumbtack.spaced.repetition.repos.UserRepository;
import net.thumbtack.spaced.repetition.repos.UsersWordRepository;
import net.thumbtack.spaced.repetition.repos.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class ControllerTestBase {

    protected User admin;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UsersWordRepository usersWordRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected WordRepository wordRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected ApplicationProperties properties;

    @BeforeEach
    public void init() {
        admin = userRepository.findByUsernameIgnoreCase("admin");
    }

    protected <T> T getResponse(MvcResult mvcResult, Class<T> clazz) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
        } catch (Exception e) {
            return null;
        }
    }

    protected <T> String toJson(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "";
        }
    }
}
