package ru.skypro.homework.controller;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.controller.util.TestUtils;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HomeworkApplication.class)
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final static int TOTAL_NUMBER_OF_PRE_CREATED_USERS = 10;
    private List<UserEntity> users;
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void beforeEach() throws Exception {
        users = TestUtils.createUniqueUsers(TOTAL_NUMBER_OF_PRE_CREATED_USERS, passwordEncoder);
        userRepository.saveAll(users);
    }

    @AfterEach
    void afterEach() {
        imageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Password change by authenticated user.")
    @Test
    void setPassword_UserRole_Ok() throws Exception {
        String currentPassword = TestUtils.getPasswords(1).get(0);
        String newPassword = TestUtils.getPasswords(1).get(0);

        UserEntity existingUser = TestUtils.getRandomUserFrom(users);
        existingUser.setPassword(passwordEncoder.encode(currentPassword));
        existingUser = userRepository.save(existingUser);

        List<UserEntity> singleUserList = new ArrayList<>();
        singleUserList.add(existingUser);

        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(singleUserList);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JSONObject changePswRequest = new JSONObject();
        changePswRequest.put("currentPassword", currentPassword);
        changePswRequest.put("newPassword", newPassword);

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePswRequest.toString()))
                .andExpectAll(
                        authenticated().withAuthenticationName(authentication.getName()),
                        status().isOk());

        String updatedPassword = userRepository.findById(existingUser.getId()).orElseThrow().getPassword();
        assertTrue(passwordEncoder.matches(newPassword, updatedPassword));
    }

    @DisplayName("Password change by authenticated admin.")
    @Test
    void setPassword_AdminRole_Ok() throws Exception {
        String currentPassword = TestUtils.getPasswords(1).get(0);
        String newPassword = TestUtils.getPasswords(1).get(0);

        UserEntity existingUser = TestUtils.createAdmin(users);
        existingUser.setPassword(passwordEncoder.encode(currentPassword));
        existingUser = userRepository.save(existingUser);

        List<UserEntity> singleUserList = new ArrayList<>();
        singleUserList.add(existingUser);

        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(singleUserList);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JSONObject changePswRequest = new JSONObject();
        changePswRequest.put("currentPassword", currentPassword);
        changePswRequest.put("newPassword", newPassword);

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePswRequest.toString()))
                .andExpectAll(
                        authenticated().withAuthenticationName(authentication.getName()),
                        status().isOk());

        String updatedPassword = userRepository.findById(existingUser.getId()).orElseThrow().getPassword();
        assertTrue(passwordEncoder.matches(newPassword, updatedPassword));
    }

    @DisplayName("Password change by authorized user with wrong repeat password.")
    @Test
    void setPassword_Forbidden() throws Exception {
        String currentPassword = TestUtils.getPasswords(1).get(0);
        String newPassword = TestUtils.getPasswords(1).get(0);

        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JSONObject changePswRequest = new JSONObject();
        changePswRequest.put("currentPassword", currentPassword);
        changePswRequest.put("newPassword", newPassword);

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePswRequest.toString()))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Password change by unauthorized user.")
    @Test
    void setPassword_Unauthorized() throws Exception {
        String currentPassword = TestUtils.getPasswords(1).get(0);
        String newPassword = TestUtils.getPasswords(1).get(0);

        JSONObject changePswRequest = new JSONObject();
        changePswRequest.put("currentPassword", currentPassword);
        changePswRequest.put("newPassword", newPassword);

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePswRequest.toString()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Obtaining information about an authorized user.")
    @Test
    void getData_AuthorizedUser() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity existingUser = userRepository.findByUsername(authentication.getName()).orElseThrow();

        mockMvc.perform(get("/users/me"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(existingUser.getId()),
                        jsonPath("$.username").value(existingUser.getUsername()),
                        jsonPath("$.firstName").value(existingUser.getFirstName()),
                        jsonPath("$.lastName").value(existingUser.getLastName()),
                        jsonPath("$.phone").value(existingUser.getPhone()),
                        jsonPath("$.role").value(existingUser.getRole().toString()),
                        jsonPath("$.image").value(existingUser.getImage()));
    }

    @DisplayName("Obtaining information about admin.")
    @Test
    void getData_AuthorizedAdmin() throws Exception {
        UserEntity admin = TestUtils.createAdmin(users);
        userRepository.save(admin);

        List<UserEntity> singleUserList = new ArrayList<>();
        singleUserList.add(admin);

        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(singleUserList);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity existingUser = userRepository.findByUsername(authentication.getName()).orElseThrow();

        mockMvc.perform(get("/users/me"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(existingUser.getId()),
                        jsonPath("$.username").value(existingUser.getUsername()),
                        jsonPath("$.firstName").value(existingUser.getFirstName()),
                        jsonPath("$.lastName").value(existingUser.getLastName()),
                        jsonPath("$.phone").value(existingUser.getPhone()),
                        jsonPath("$.role").value(existingUser.getRole().toString()),
                        jsonPath("$.image").value(existingUser.getImage()));
    }

    @DisplayName("Obtaining user information by non-authorized user.")
    @Test
    void getData_Unauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Update information about an authorized user.")
    @Test
    void updateData_AuthorizedUser() throws Exception {
        UserEntity user = TestUtils.getRandomUserFrom(users);

        List<UserEntity> singleUserList = new ArrayList<>();
        singleUserList.add(user);

        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(singleUserList);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String firstName = TestUtils.getFirstNames(1).get(0);
        String LastName = TestUtils.getLastNames(1).get(0);
        String phone = TestUtils.getPhones(1).get(0);

        JSONObject updateData = new JSONObject();
        updateData.put("firstName", firstName);
        updateData.put("lastName", LastName);
        updateData.put("phone", phone);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateData.toString()))
                .andExpectAll(status().isOk(),
                        authenticated().withAuthenticationName(authentication.getName()),
                        jsonPath("$.firstName").value(firstName),
                        jsonPath("$.lastName").value(LastName),
                        jsonPath("$.phone").value(phone));
    }

    @DisplayName("Update information about by non-authorized user.")
    @Test
    void updateData_UnAuthorizedUser() throws Exception {
        String firstName = TestUtils.getFirstNames(1).get(0);
        String LastName = TestUtils.getLastNames(1).get(0);
        String phone = TestUtils.getPhones(1).get(0);

        JSONObject updateData = new JSONObject();
        updateData.put("firstName", firstName);
        updateData.put("lastName", LastName);
        updateData.put("phone", phone);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateData.toString()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Updating the profile image of an authorized user.")
    @Test
    void updateImage_AuthorizedUser() throws Exception {
        Authentication authentication = TestUtils.createAuthenticationTokenForRandomUser(users);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());

        mockMvc.perform(multipart("/users/me/image")
                        .file(imageFile)
                        .with(
                                request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                }
                        )
                )
                .andExpect(status().isOk());
    }

    @DisplayName("Updating the profile image by non-authorized user.")
    @Test
    void updateImage_UnauthorizedUser() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("image", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());
        mockMvc.perform(
                        multipart("/users/me/image")
                                .file(imageFile)
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        }
                                )
                )
                .andExpect(status().isUnauthorized());
    }
}
