package ru.skypro.homework.dto.ads;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class CreateOrUpdateAd {
    @Size(min = 4, max = 32)
    private String title;
    @Min(0) @Max(10000000)
    private Integer price;
    @Size(min = 8, max = 64)
    private String description;
}
