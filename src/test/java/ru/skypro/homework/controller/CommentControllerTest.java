package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.dto.user.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HomeworkApplication.class)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CommentControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdRepository adRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ImageRepository imageRepository;
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine");
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
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @DisplayName("Проверка работоспособности соединения с БД PostgreSQL.")
    @Test
    void testPostgresql() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn).isNotNull();
        }
    }

    @DisplayName("Получение всех комментариев под объявлением неавторизованным пользователем")
    @Test
    void getAllCommentsForAdTestNegative() throws Exception {
        int id = TestUtils.getRandomExistedAd(ads).getPk();

        mockMvc.perform(get("/ads/{id}/comments", id))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Получение всех комментариев под объявленеим авторизированным пользователем")
    @Test
    void getAllCommentsForAdTest() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<Comment> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .map(commentMapper::toComment)
                .collect(Collectors.toList());
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String json = objectMapper.writeValueAsString(commentMapper.toComments(commentEntityListWithPkAd));
        mockMvc.perform(get("/ads/{id}/comments", adEntity.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

    }

    @DisplayName("Добавление комментария под объявление авторизированным пользователем")
    @Test
    void createCommentTest() throws Exception {
        UserEntity currentUser = TestUtils.getRandomUser(users);
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New Text23");
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        Comment currentComment = new Comment();
        currentComment.setAuthor(currentUser.getId());
        currentComment.setText(createOrUpdateComment.getText());
        currentComment.setAuthorFirstName(currentUser.getFirstName());
        currentComment.setAuthorImage(currentUser.getImage());
        long countFirst = commentRepository.count();
        mockMvc.perform(post("/ads/{id}/comments", adEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(currentComment.getText()))
                .andExpect(jsonPath("$.authorFirstName").value(currentComment.getAuthorFirstName()))
                .andExpect(jsonPath("$.author").value(currentComment.getAuthor()));
        long countAfter = commentRepository.count();
        assertEquals(countFirst + 1, countAfter);
    }

    @DisplayName("Добавление комментария под объявление неавторизированным пользователем")
    @Test
    void createCommentTestNegative() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New Text23");
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        long countFirst = commentRepository.count();
        mockMvc.perform(post("/ads/{id}/comments", adEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
        long countAfter = commentRepository.count();
        assertEquals(countFirst, countAfter);
    }

    @DisplayName("Добавление комментария под объявление с плохим запросом авторизированным пользователем")
    @Test
    void createCommentTestNegativeBadRequest() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New");
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        long countFirst = commentRepository.count();
        mockMvc.perform(post("/ads/{id}/comments", adEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isBadRequest());
        long countAfter = commentRepository.count();
        assertEquals(countFirst, countAfter);
    }

    @DisplayName("Удаление своего комментария авторизированным пользователем")
    @Test
    void deleteCommentTest() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        List<UserEntity> userEntities = users.stream()
                .filter(a -> a.getId().equals(commentEntity.getAuthor().getId()))
                .collect(Collectors.toList());
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(userEntities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long countFirst = commentRepository.count();
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk());
        long countAfter = commentRepository.count();
        assertEquals(countFirst - 1, countAfter);
    }

    @DisplayName("Удаление не своего комментария авторизированным пользователем")
    @Test
    void deleteCommentTestNegative() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        List<UserEntity> userEntities = users.stream()
                .filter(a -> !a.getId().equals(commentEntity.getAuthor().getId()))
                .filter(a -> !a.getRole().equals(Role.ADMIN))
                .collect(Collectors.toList());
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(userEntities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long countFirst = commentRepository.count();
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isForbidden());
        long countAfter = commentRepository.count();
        assertEquals(countFirst, countAfter);
    }

    @DisplayName("Удаление комментария авторизированным пользователем с ролью Админ")
    @Test
    void deleteCommentTestWithAdminRole() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long countFirst = commentRepository.count();
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk());
        long countAfter = commentRepository.count();
        assertEquals(countFirst - 1, countAfter);
    }

    @DisplayName("Удаление несуществующего комментария авторизованным пользователем")
    @Test
    void deleteCommentTestIfNotFound() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> !a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long countFirst = commentRepository.count();
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isNotFound());
        long countAfter = commentRepository.count();
        assertEquals(countFirst, countAfter);
    }

    @DisplayName("Обновление своего комментария авторизованным пользователем")
    @Test
    void updateCommentTest() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        List<UserEntity> userEntities = users.stream()
                .filter(a -> a.getId().equals(commentEntity.getAuthor().getId()))
                .collect(Collectors.toList());
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(userEntities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New Text23");
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        commentEntity.setText(createOrUpdateComment.getText());
        String currentComment = objectMapper.writeValueAsString(commentMapper.toComment(commentEntity));
        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(content().json(currentComment))
                .andExpect(status().isOk());
    }

    @DisplayName("Обновление не своего комментария авторизованным пользователем")
    @Test
    void updateCommentTestNegative() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        List<UserEntity> userEntities = users.stream()
                .filter(a -> !a.getId().equals(commentEntity.getAuthor().getId()))
                .filter(a -> !a.getRole().equals(Role.ADMIN))
                .collect(Collectors.toList());
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(userEntities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New Text23");
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        commentEntity.setText(createOrUpdateComment.getText());
        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Обновление комментария авторизованным пользователем с ролью админ")
    @Test
    void updateCommentTestWithAdminRole() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText(TestUtils.getRandomExistedComment(comments).getText());
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        commentEntity.setText(createOrUpdateComment.getText());
        String currentComment = objectMapper.writeValueAsString(commentMapper.toComment(commentEntity));
        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(content().json(currentComment))
                .andExpect(status().isOk());
    }

    @DisplayName("Обновление несуществующего комментария авторизованным пользователем")
    @Test
    void updateCommentTestIfNotFound() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> !a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText(TestUtils.getRandomExistedComment(comments).getText());
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        commentEntity.setText(createOrUpdateComment.getText());
        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("")
    @Test
    void updateCommentTestNegativeBadRequest() throws Exception {
        AdEntity adEntity = TestUtils.getRandomExistedAd(ads);
        List<CommentEntity> commentEntityListWithPkAd = comments.stream()
                .filter(a -> a.getAdEntity().getPk().equals(adEntity.getPk()))
                .collect(Collectors.toList());
        CommentEntity commentEntity = TestUtils.getRandomExistedComment(commentEntityListWithPkAd);
        List<UserEntity> userEntities = users.stream()
                .filter(a -> a.getId().equals(commentEntity.getAuthor().getId()))
                .collect(Collectors.toList());
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(userEntities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New");
        String json = objectMapper.writeValueAsString(createOrUpdateComment);
        commentEntity.setText(createOrUpdateComment.getText());

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", adEntity.getPk(), commentEntity.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isBadRequest());
    }
}
