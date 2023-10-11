package ru.skypro.homework.controller;

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

    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(Authentication authentication, @RequestBody @Valid NewPassword newPassword) {
        userService.setPassword(authentication, newPassword);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getData(Authentication authentication) {
        User user = userService.getData(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateData(Authentication authentication, @RequestBody @Valid UpdateUser updateUser) {
        UpdateUser updateUserReturn = userService.updateData(authentication, updateUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(updateUserReturn);
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateImage(Authentication authentication, @RequestParam MultipartFile image) {
        userService.updateImage(authentication, image);

        return ResponseEntity.ok().build();
    }
}
