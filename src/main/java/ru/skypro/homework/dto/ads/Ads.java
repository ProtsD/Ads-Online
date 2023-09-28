package ru.skypro.homework.dto.ads;

import lombok.Data;

@Data
public class Ads {
    private int count;
    private Ad[] results;
}
