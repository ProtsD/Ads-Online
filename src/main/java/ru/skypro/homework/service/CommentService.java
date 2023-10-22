package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;

public interface CommentService {
    /**
     * returns all comment under ad
     *
     * @param authentication the currently authenticated principal, or an authentication request token
     * @param id             ad id
     * @return get all comment under ad
     */
    Comments getAllComments(Authentication authentication, Integer id);

    /**
     * create and return comment
     *
     * @param authentication the currently authenticated principal, or an authentication request token
     * @param id             ad id
     * @param comment        properties for new comment
     * @return info by created comment
     */

    Comment createComment(Authentication authentication, Integer id, CreateOrUpdateComment comment);

    /**
     * delete comment
     *
     * @param authentication the currently authenticated principal, or an authentication request token
     * @param adId           ad id
     * @param commentId      deleted comment id
     * @throws ru.skypro.homework.exception.ForbiddenException if user has no access to the current comment
     * @throws ru.skypro.homework.exception.NotFoundException  if no value is found
     */
    void deleteComment(Authentication authentication, Integer adId, Integer commentId);

    /**
     * update comment
     *
     * @param authentication        the currently authenticated principal, or an authentication request token
     * @param adId                  ad id
     * @param commentId             update comment id
     * @param createOrUpdateComment properties for update comment
     * @return info on update comment
     * @throws ru.skypro.homework.exception.ForbiddenException if user has no access to the current comment
     * @throws ru.skypro.homework.exception.NotFoundException  if no value is found
     */
    Comment updateComment(Authentication authentication, Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment);
}
