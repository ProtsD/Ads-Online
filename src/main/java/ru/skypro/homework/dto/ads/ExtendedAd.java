package ru.skypro.homework.dto.ads;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class ExtendedAd {
    private int pk;
    private String authorFirstName;
    private String authorLastName;
    @Size(min = 8, max = 64)
    private String description;
    private String email;
    private String image;
    private String phone;
    @Min(0) @Max(10000000)
    private int price;
    @Size(min = 4, max = 32)
    private String title;
}
