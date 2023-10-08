package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.user.NewPassword;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.User;

public interface UserService {
    void setPassword(Authentication authentication, NewPassword newPassword);

    User getData(Authentication authentication);

    UpdateUser updateData(Authentication authentication, UpdateUser updateUser);

    void updateImage(Authentication authentication, MultipartFile image);
}
