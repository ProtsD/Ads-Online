package ru.skypro.homework.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Login {

    private String username;
    private String password;
}
