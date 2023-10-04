package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;

public interface CommentService {
    Comments getAllComments(Authentication authentication,int id);

    Comment createComment(Authentication authentication, int id, CreateOrUpdateComment comment);

    void deleteComment(Authentication authentication,int adId, int commentId);

    Comment updateComment(Authentication authentication,int adId, int commentId, CreateOrUpdateComment createOrUpdateComment);
}
