package ru.skypro.homework.dto.comment;

import lombok.Data;

@Data
public class Comment {
    int idAuthor;
    String authorImage;
    String firstNameAuthor;
    int timeCreateComment;
    int idComment;
    String description;
}
