package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.user.NewPassword;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.User;

public interface UserService {
    /**
     * Sets password.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param newPassword    the new password
     * @throws ru.skypro.homework.exception.ForbiddenException if user has entered wrong old password
     */
    void setPassword(Authentication authentication, NewPassword newPassword);

    /**
     * Returns authenticated user data.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @return user data
     */
    User getData(Authentication authentication);

    /**
     * Returns authenticated user data.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param updateUser     the data to be updated.
     * @return updated user data
     */
    UpdateUser updateData(Authentication authentication, UpdateUser updateUser);

    /**
     * Updates user avatar.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param image          MultipartFile for image
     */
    void updateImage(Authentication authentication, MultipartFile image);
}
