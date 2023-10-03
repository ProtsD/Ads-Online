package ru.skypro.homework.mapper;

import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.UserEntity;

public class UserMapper {
    public User toUser(UserEntity userEntity) {
        return new User()
                .setId(userEntity.getId())
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setPhone(userEntity.getPhone())
                .setRole(userEntity.getRole())
                .setImage(userEntity.getImage());
    }
    public UpdateUser toUpdateUser(UserEntity userEntity) {
        return new UpdateUser()
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setPhone(userEntity.getPhone());
    }
}