package ru.skypro.homework.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Component
@Data
@Accessors(chain = true)
public class FullUserInfo {
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private String image;
}
