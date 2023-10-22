package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.user.NewPassword;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Обновление пароля", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(Authentication authentication, @RequestBody @Valid NewPassword newPassword) {
        userService.setPassword(authentication, newPassword);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение информации об авторизованном пользователе", tags={ "Пользователи" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized") })
    @GetMapping("/me")
    public ResponseEntity<User> getData(Authentication authentication) {
        User user = userService.getData(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(user);
    }
    @Operation(summary = "Обновление информации об авторизованном пользователе", tags={ "Пользователи" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UpdateUser.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized") })
    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateData(Authentication authentication, @RequestBody @Valid UpdateUser updateUser) {
        UpdateUser updateUserReturn = userService.updateData(authentication, updateUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(updateUserReturn);
    }
    @Operation(summary = "Обновление аватара авторизованного пользователя", tags={ "Пользователи" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized") })
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateImage(Authentication authentication, @RequestParam MultipartFile image) {
        userService.updateImage(authentication, image);

        return ResponseEntity.ok().build();
    }
}
