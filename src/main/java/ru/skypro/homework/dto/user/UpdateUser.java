package ru.skypro.homework.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Component
@Data
@Accessors(chain = true)
public class UpdateUser {
    @Size(min = 2, max = 16)
    private String firstName;
    @Size(min = 2, max = 16)
    private String lastName;
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;
}
