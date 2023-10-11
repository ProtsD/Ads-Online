package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;

public interface CommentService {
    Comments getAllComments(Authentication authentication, Integer id);

    Comment createComment(Authentication authentication, Integer id, CreateOrUpdateComment comment);

    void deleteComment(Authentication authentication, Integer adId, Integer commentId);

    Comment updateComment(Authentication authentication, Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment);
}
