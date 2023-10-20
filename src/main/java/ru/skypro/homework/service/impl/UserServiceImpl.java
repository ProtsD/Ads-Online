package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.user.FullUserInfo;
import ru.skypro.homework.dto.user.NewPassword;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.UnauthorizedException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.util.ServiceUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final ServiceUtils serviceUtils;

    @Override
    public void setPassword(Authentication authentication, NewPassword newPassword) {
        UserEntity currentUser = userMapper.toEntity(serviceUtils.getCurrentUser(authentication));

        if (passwordEncoder.matches(newPassword.getCurrentPassword(), currentUser.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
            userRepository.save(currentUser);
        } else {
            throw new ForbiddenException("Wrong password");
        }
    }

    @Override
    public User getData(Authentication authentication) {
        return userMapper.toUser(serviceUtils.getCurrentUser(authentication));
    }

    @Override
    public UpdateUser updateData(Authentication authentication, UpdateUser updateUser) {
        FullUserInfo currentUser = serviceUtils.getCurrentUser(authentication)
                .setFirstName(updateUser.getFirstName())
                .setLastName(updateUser.getLastName())
                .setPhone(updateUser.getPhone());
        userRepository.save(userMapper.toEntity(currentUser));

        return updateUser;
    }

    @Override
    public void updateImage(Authentication authentication, MultipartFile image) {
        UserEntity currentUser = userMapper.toEntity(serviceUtils.getCurrentUser(authentication));
        String imageURL;
        ImageEntity imageEntity;

        try {
            byte[] imageBytes = image.getBytes();
            if (currentUser.getImage() == null) {
                imageEntity = imageService.uploadImage(imageBytes);
            } else {
                Integer imageId = Integer.valueOf(currentUser.getImage().replaceAll(ImageService.IMAGE_URL_PREFIX, ""));
                imageEntity = imageService.updateImage(imageId, imageBytes);
            }

            imageURL = ImageService.IMAGE_URL_PREFIX + imageEntity.getId();
            currentUser.setImage(imageURL);

            userRepository.save(currentUser);

        } catch (IOException | NumberFormatException e) {
            log.debug(e.getMessage());
        }
    }
}
