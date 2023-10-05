package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.UserEntity;

@Component
public class UserMapper {
    public User toUser(UserEntity userEntity) {
        return new User()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setPassword(userEntity.getPassword())
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setPhone(userEntity.getPhone())
                .setRole(userEntity.getRole())
                .setImage(userEntity.getImage());
//                .setUsername(userEntity.getUsername())

    }

    public UpdateUser toUpdateUser(UserEntity userEntity) {
        return new UpdateUser()
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setPhone(userEntity.getPhone());
    }
}