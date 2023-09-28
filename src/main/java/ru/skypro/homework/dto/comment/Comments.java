package ru.skypro.homework.dto.comment;

import lombok.Data;

@Data
public class Comments {
    int countComments;
    Comment[] comments;
}
