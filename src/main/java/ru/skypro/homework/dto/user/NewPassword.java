package ru.skypro.homework.dto.user;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;

@Component
@Data
public class NewPassword {
    @Size(min = 8, max = 16)
    private String currentPassword;
    @Size(min = 8, max = 16)
    private String newPassword;
}
