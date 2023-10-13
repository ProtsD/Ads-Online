package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.user.FullUserInfo;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.UserEntity;

@Component
public class UserMapper {
    public UserEntity toEntity(FullUserInfo user) {
        return new UserEntity()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhone(user.getPhone())
                .setRole(user.getRole())
                .setImage(user.getImage());
    }

    public User toUser(UserEntity userEntity) {
        return new User()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setPhone(userEntity.getPhone())
                .setRole(userEntity.getRole())
                .setImage(userEntity.getImage());
    }

    public User toUser(FullUserInfo fullUserInfo) {
        return new User()
                .setId(fullUserInfo.getId())
                .setUsername(fullUserInfo.getUsername())
                .setFirstName(fullUserInfo.getFirstName())
                .setLastName(fullUserInfo.getLastName())
                .setPhone(fullUserInfo.getPhone())
                .setRole(fullUserInfo.getRole())
                .setImage(fullUserInfo.getImage());
    }

    public FullUserInfo toFullUserInfo(UserEntity userEntity){
        return new FullUserInfo()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setPassword(userEntity.getPassword())
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