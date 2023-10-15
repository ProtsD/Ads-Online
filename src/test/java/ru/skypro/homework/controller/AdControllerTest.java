package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.controller.util.TestUtils;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.ImageServiceImpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HomeworkApplication.class)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AdControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdRepository adRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageServiceImpl imageService;
    @Autowired
    private DataSource dataSource;
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine");
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdMapper adMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final static int TOTAL_NUMBER_OF_PRE_CREATED_USERS = 50;
    private final static int TOTAL_NUMBER_OF_PRE_CREATED_ADS = 10;
    private final static int MAX_NUMBER_OF_PRE_CREATED_COMMENTS_FOR_SINGLE_AD = 10;
    private List<UserEntity> users;
    private List<AdEntity> ads;
    private List<CommentEntity> comments;
    private UserEntity admin;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void beforeEach() throws Exception {
        //TODO
        users = TestUtils.createUniqueUsers(TOTAL_NUMBER_OF_PRE_CREATED_USERS, passwordEncoder);
        admin = TestUtils.createAdmin(users);
        userRepository.saveAll(users);

        ads = TestUtils.createAds(TOTAL_NUMBER_OF_PRE_CREATED_ADS, users, imageService);
        adRepository.saveAll(ads);

        comments = TestUtils.createComments(MAX_NUMBER_OF_PRE_CREATED_COMMENTS_FOR_SINGLE_AD, users, ads);
        commentRepository.saveAll(comments);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        adRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Проверка работоспособности соединения с БД PostgreSQL.")
    @Test
    void testPostgresql() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn).isNotNull();
        }
    }

    @DisplayName("Временная заглушка.")
    @Test
    void testStub() throws Exception {
        UserEntity currentUser = admin;
        String expectedUserJson = objectMapper.writeValueAsString(userMapper.toUser(currentUser));

        for (UserEntity currentTempUser : users) {
            System.out.println(currentTempUser);
        }

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println(currentUser);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        for (AdEntity currentAd : ads) {
            System.out.println(currentAd);
        }
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        for (CommentEntity comment : comments) {
            System.out.println(comment);
        }
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        SecurityContextHolder.getContext().setAuthentication(TestUtils.createAuthenticationTokenForUser(currentUser));
        mockMvc.perform(get("/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedUserJson));

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        mockMvc.perform(get("/ads/me"))
                .andDo(print())
                .andExpect(status().isOk());

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        SecurityContextHolder.getContext().setAuthentication(null);
        mockMvc.perform(get("/ads"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
