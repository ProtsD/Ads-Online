package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.el.stream.Stream;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.mappers.CommentMapper;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private CommentMapper commentMapper;
    private CommentRepository commentRepository;
    @Override
    public Comments getAllComments(Authentication authentication,int id) {
        List<Comment> commentList= StreamSupport.stream(commentRepository.findAll().spliterator(),false)
                .filter(n -> n.getAdsEntity().getId()==id)
                .map(commentMapper::toComment)
                .collect(Collectors.toList());
        return commentMapper.toComments(commentList);
    }

    @Override
    public Comment createComment(Authentication authentication, int id, CreateOrUpdateComment comment) {
        return null;
    }

    @Override
    public void deleteComment(Authentication authentication,int adId, int commentId) {

    }

    @Override
    public Comment updateComment(Authentication authentication,int adId, int commentId, CreateOrUpdateComment createOrUpdateComment) {
        return null;
    }
}
