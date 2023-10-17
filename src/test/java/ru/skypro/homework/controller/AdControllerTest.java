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
import org.springframework.mock.web.MockMultipartFile;
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
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.user.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.util.ServiceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ImageService imageService;
    @Autowired
    private DataSource dataSource;
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine");
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdMapper adMapper;
    @Autowired
    private ServiceUtils serviceUtils;
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

    @DisplayName("Check database connection.")
    @Test
    void testPostgresql() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn).isNotNull();
        }
    }

    @DisplayName("Obtain all ads by unauthorised user.")
    @Test
    void getAllAds_getRequest_withoutAuthorization_thenJsonVariable() throws Exception {
        List<Ad> adList = adMapper.toAdList(ads);
        String expectedJSON = objectMapper.writeValueAsString(adMapper.toAds(adList));

        mockMvc.perform(get("/ads"))
                .andExpect(unauthenticated())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJSON));
    }

    @DisplayName("Obtain all ads by authorised user.")
    @Test
    void getAllAds_getRequest_withAuthorization_thenJsonVariable() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<Ad> adList = adMapper.toAdList(ads);
        String expectedJSON = objectMapper.writeValueAsString(adMapper.toAds(adList));

        mockMvc.perform(get("/ads"))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJSON));
    }

    @DisplayName("Add ads by unauthorised user.")
    @Test
    void addAd_postRequest_withoutAuthorization_thenUnauthorized() throws Exception {
        CreateOrUpdateAd newAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");
        String newAdJsonValue = objectMapper.writeValueAsString(newAd);
        long countBefore = adRepository.count();

        MockMultipartFile propertiesFile = new MockMultipartFile("properties", "file1.json", MediaType.APPLICATION_JSON_VALUE, newAdJsonValue.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(multipart("/ads").file(propertiesFile).file(imageFile))
                .andExpect(unauthenticated())
                .andExpect(status().isUnauthorized());

        long countAfter = adRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @DisplayName("Add ad by authorised user.")
    @Test
    void addAd_postRequest_withAuthorization_thenJsonVariable() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long countBefore = adRepository.count();
        int numberOfCreatedItems = 1;

        CreateOrUpdateAd newAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");
        String newAdJsonValue = objectMapper.writeValueAsString(newAd);

        MockMultipartFile propertiesFile = new MockMultipartFile("properties", "file1.json", MediaType.APPLICATION_JSON_VALUE, newAdJsonValue.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(multipart("/ads").file(propertiesFile).file(imageFile))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isCreated());

        long countAfter = adRepository.count();
        assertEquals(countBefore + numberOfCreatedItems, countAfter);
    }

    @DisplayName("Add ad with incorrect data by authorised user.")
    @Test
    void addAd_postRequest_withAuthorization_withIncorrectData_thenBadRequest() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long countBefore = adRepository.count();

        CreateOrUpdateAd newIncorrectAd = new CreateOrUpdateAd().setTitle("Title").setPrice(0).setDescription("Desc");
        String newAdJsonValue = objectMapper.writeValueAsString(newIncorrectAd);

        MockMultipartFile propertiesFile = new MockMultipartFile("properties", "file1.json", MediaType.APPLICATION_JSON_VALUE, newAdJsonValue.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(multipart("/ads").file(propertiesFile).file(imageFile))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isBadRequest());

        long countAfter = adRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @DisplayName("Obtain ad data by unauthorised user.")
    @Test
    void getAdInfo_withoutAuthorization_thenUnauthorized() throws Exception {
        int existedAdId = TestUtils.getRandomExistedAd(ads).getPk();

        mockMvc.perform(get("/ads/{id}", existedAdId))
                .andExpect(unauthenticated())
                .andExpect(status().isUnauthorized());

    }

    @DisplayName("Obtain ad data by authorised user.")
    @Test
    void getAdInfo_withAuthorization_thenJsonVariable() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);

        String expectedAdJsonValue = objectMapper.writeValueAsString(adMapper.toExtendedAd(existedAd));

        mockMvc.perform(get("/ads/{id}", existedAd.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedAdJsonValue));
    }

    @DisplayName("Obtain non-existent ad data by authorised user.")
    @Test
    void getAdInfo_withAuthorization_thenNotFound() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int nonExistentId = TestUtils.getRandomNonExistentAdId(ads);

        mockMvc.perform(get("/ads/{id}", nonExistentId))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isNotFound());

    }

    @DisplayName("Delete ad by unauthorized user.")
    @Test
    void deleteAd_withoutAuthorization_thenUnauthorized() throws Exception {
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);
        long countBefore = adRepository.count();

        mockMvc.perform(delete("/ads/{id}", existedAd.getPk()))
                .andExpect(unauthenticated())
                .andExpect(status().isUnauthorized());

        long countAfter = adRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @DisplayName("Delete their own ad by unauthorized user.")
    @Test
    void deleteAd_withAuthorization_ownAd_thenNoContent() throws Exception {
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(existedAd.getAuthor());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        long adsCountBefore = adRepository.count();
        long commentsCountBefore = commentRepository.count();
        int numberOfDeletedAds = 1;

        int commentsCountForCurrentAd = 0;
        for (CommentEntity currentComment : comments) {
            if (Objects.equals(currentComment.getAdEntity(), existedAd)) {
                commentsCountForCurrentAd++;
            }
        }

        mockMvc.perform(delete("/ads/{id}", existedAd.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isNoContent());

        long adsCountAfter = adRepository.count();
        long commentCountAfter = commentRepository.count();

        assertEquals(adsCountBefore - numberOfDeletedAds, adsCountAfter);
        assertEquals(commentsCountBefore - commentsCountForCurrentAd, commentCountAfter);
    }

    @DisplayName("The unauthorized user cannot delete someone else's ad.")
    @Test
    void deleteAd_withAuthorization_someoneElseAd_thenForbidden() throws Exception {
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);
        Authentication authentication;
        long countBefore = adRepository.count();

        do {
            authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        } while (existedAd.getAuthor().getId() == serviceUtils.getCurrentUser(authentication).getId()
                || serviceUtils.getCurrentUser(authentication).getRole().equals(Role.ADMIN));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/ads/{id}", existedAd.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isForbidden());

        long countAfter = adRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @DisplayName("An authorized user with the role of administrator can delete someone else's ad.")
    @Test
    void deleteAd_withAuthorization_someoneElseAd_withAdminRole_thenNoContent() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AdEntity existedAd;
        do {
            existedAd = TestUtils.getRandomExistedAd(ads);
        } while (Objects.equals(existedAd.getAuthor().getId(), admin.getId()));

        long adsCountBefore = adRepository.count();
        long commentsCountBefore = commentRepository.count();
        int numberOfDeletedAds = 1;

        int commentsCountForCurrentAd = 0;
        for (CommentEntity currentComment : comments) {
            if (Objects.equals(currentComment.getAdEntity(), existedAd)) {
                commentsCountForCurrentAd++;
            }
        }

        mockMvc.perform(delete("/ads/{id}", existedAd.getPk()))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()).withRoles("ADMIN"))
                .andExpect(status().isNoContent());

        long adsCountAfter = adRepository.count();
        long commentCountAfter = commentRepository.count();

        assertEquals(adsCountBefore - numberOfDeletedAds, adsCountAfter);
        assertEquals(commentsCountBefore - commentsCountForCurrentAd, commentCountAfter);
    }

    @DisplayName("An authorized user cannot delete a nonexistent ad.")
    @Test
    void deleteAd_withAuthorization_nonExistentAd_thenNotFound() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int nonExistentId = TestUtils.getRandomNonExistentAdId(ads);
        long countBefore = adRepository.count();

        mockMvc.perform(delete("/ads/{id}", nonExistentId))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isNotFound());

        long countAfter = adRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @DisplayName("An authorized user with administrator privileges cannot delete a nonexistent ad.")
    @Test
    void deleteAd_withAuthorization_nonExistentAd_withAdminRole_thenNotFound() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int nonExistentId = TestUtils.getRandomNonExistentAdId(ads);
        long countBefore = adRepository.count();

        mockMvc.perform(delete("/ads/{id}", nonExistentId))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()).withRoles("ADMIN"))
                .andExpect(status().isNotFound());

        long countAfter = adRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @DisplayName("Update ad data by unauthorised user.")
    @Test
    void updateAdInfo_withoutAuthorization_thenUnauthorized() throws Exception {
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");
        int existedAdId = TestUtils.getRandomExistedAd(ads).getPk();

        mockMvc.perform(
                        patch("/ads/{id}", existedAdId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(updateAd))
                )
                .andExpect(unauthenticated())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Update their own ad by authorized user.")
    @Test
    void updateAdInfo_withAuthorization_ownAd_thenJsonVariable() throws Exception {
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);
        Ad expectedAd = new Ad()
                .setAuthor(existedAd.getAuthor().getId())
                .setImage(existedAd.getImage())
                .setPk(existedAd.getPk())
                .setPrice(updateAd.getPrice())
                .setTitle(updateAd.getTitle());
        String expectedJson = objectMapper.writeValueAsString(expectedAd);

        Authentication authentication = TestUtils.createAuthenticationTokenForUser(existedAd.getAuthor());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(
                        patch("/ads/{id}", existedAd.getPk())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(updateAd))
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(content().json(expectedJson))
                .andExpect(status().isOk());
    }

    @DisplayName("Update someone else's ad by authorized user.")
    @Test
    void updateAdInfo_withAuthorization_someoneElseAd_thenForbidden() throws Exception {
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");
        Authentication authentication;

        do {
            authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        } while (existedAd.getAuthor().getId() == serviceUtils.getCurrentUser(authentication).getId()
                || serviceUtils.getCurrentUser(authentication).getRole().equals(Role.ADMIN));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(
                        patch("/ads/{id}", existedAd.getPk())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(updateAd))
                )
                .andExpect(status().isForbidden());
    }

    @DisplayName("Update someone else's ad by authorized admin.")
    @Test
    void updateAdInfo_withAuthorization_someoneElseAd_withAdminRole_thenJsonVariable() throws Exception {
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");

        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AdEntity existedAd;
        do {
            existedAd = TestUtils.getRandomExistedAd(ads);
        } while (Objects.equals(existedAd.getAuthor().getId(), admin.getId()));

        Ad expectedAd = new Ad()
                .setAuthor(existedAd.getAuthor().getId())
                .setImage(existedAd.getImage())
                .setPk(existedAd.getPk())
                .setPrice(updateAd.getPrice())
                .setTitle(updateAd.getTitle());

        String expectedJson = objectMapper.writeValueAsString(expectedAd);

        mockMvc.perform(
                        patch("/ads/{id}", existedAd.getPk())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(updateAd))
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()).withRoles("ADMIN"))
                .andExpect(content().json(expectedJson))
                .andExpect(status().isOk());
    }

    @DisplayName("Update nonexistent ad by authorized user.")
    @Test
    void updateAdInfo_withAuthorization_nonExistentAd_thenNotFound() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int nonExistentId = TestUtils.getRandomNonExistentAdId(ads);
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");

        mockMvc.perform(
                        patch("/ads/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(updateAd))
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Update nonexistent ad by authorized admin.")
    @Test
    void updateAdInfo_withAuthorization_nonExistentAd_withAdminRole_thenNotFound() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int nonExistentId = TestUtils.getRandomNonExistentAdId(ads);
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd().setTitle("Title 001").setPrice(123).setDescription("Description 123");

        mockMvc.perform(
                        patch("/ads/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(updateAd))
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()).withRoles("ADMIN"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Update with wrong data their own ad by authorized admin.")
    @Test
    void updateAdInfo_withAuthorization_ownAd_withIncorrectData_thenBadRequest() throws Exception {
        CreateOrUpdateAd newIncorrectAd = new CreateOrUpdateAd().setTitle("Title").setPrice(0).setDescription("Desc");
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);

        Authentication authentication = TestUtils.createAuthenticationTokenForUser(existedAd.getAuthor());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(
                        patch("/ads/{id}", existedAd.getPk())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(newIncorrectAd))
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Obtain ads by authorized user.")
    @Test
    void getCurrentUserAds_withAuthorization_thenJsonVariable() throws Exception {
        UserEntity randomUserWithAds = ads.get(new Random().nextInt(ads.size())).getAuthor();
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(randomUserWithAds);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<AdEntity> currentUserAds = new ArrayList<>();
        for (AdEntity currentAd : ads) {
            if (Objects.equals(currentAd.getAuthor(), randomUserWithAds)) {
                currentUserAds.add(currentAd);
            }
        }

        String expectedJson = objectMapper.writeValueAsString(adMapper.toAds(adMapper.toAdList(currentUserAds)));

        mockMvc.perform(get("/ads/me"))
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
    @DisplayName("Obtain ads of authorised user by authorized user.")
    @Test
    void getCurrentUserAds_withoutAuthorization_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/ads/me"))
                .andExpect(unauthenticated())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Update ad picture by unauthorized user.")
    @Test
    void updateAdImage_withoutAuthorization_thenUnauthorized() throws Exception {
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(
                        multipart("/ads/{id}/image", existedAd.getPk())
                                .file(imageFile)
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        }
                                )
                )
                .andExpect(unauthenticated())
                .andExpect(status().isUnauthorized());
    }
    @DisplayName("Update picture their own ad by authorized user.")
    @Test
    void updateAdImage_withAuthorization_ownAd_thenJsonVariable() throws Exception {
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(existedAd.getAuthor());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(
                        multipart("/ads/{id}/image", existedAd.getPk())
                                .file(imageFile)
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        }
                                )
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isOk());
    }

    @DisplayName("Обновление картинки чужого объявления авторизованным пользователем.")
    @Test
    void updateAdImage_withAuthorization_someoneElseAd_thenForbidden() throws Exception {
        AdEntity existedAd = TestUtils.getRandomExistedAd(ads);

        Authentication authentication;
        do {
            authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        } while (existedAd.getAuthor().getId() == serviceUtils.getCurrentUser(authentication).getId()
                || serviceUtils.getCurrentUser(authentication).getRole().equals(Role.ADMIN));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(
                        multipart("/ads/{id}/image", existedAd.getPk())
                                .file(imageFile)
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        }
                                )
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Обновление картинки чужого объявления авторизованным пользователем с ролью администратора.")
    @Test
    void updateAdImage_withAuthorization_someoneElseAd_withAdminRole_thenJsonVariable() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AdEntity existedAd;
        do {
            existedAd = TestUtils.getRandomExistedAd(ads);
        } while (Objects.equals(existedAd.getAuthor().getId(), admin.getId()));

        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(
                        multipart("/ads/{id}/image", existedAd.getPk())
                                .file(imageFile)
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        }
                                )
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()).withRoles("ADMIN"))
                .andExpect(status().isOk());
    }

    @DisplayName("Обновление картинки несуществующего объявления авторизованным пользователем.")
    @Test
    void updateAdImage_withAuthorization_nonExistentAd_thenNotFound() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int nonExistentId = TestUtils.getRandomNonExistentAdId(ads);
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(
                        multipart("/ads/{id}/image", nonExistentId)
                                .file(imageFile)
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        }
                                )
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Обновление картинки несуществующего объявления авторизованным пользователем с ролью администратора.")
    @Test
    void updateAdImage_withAuthorization_nonExistentAd_withAdminRole_thenNotFound() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForUser(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int nonExistentId = TestUtils.getRandomNonExistentAdId(ads);
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(
                        multipart("/ads/{id}/image", nonExistentId)
                                .file(imageFile)
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        }
                                )
                )
                .andExpect(authenticated().withAuthenticationName(authentication.getName()).withRoles("ADMIN"))
                .andExpect(status().isNotFound());
    }
}
