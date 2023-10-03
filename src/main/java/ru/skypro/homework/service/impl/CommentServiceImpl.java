package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public Comments getAllComments(Authentication authentication, int id) { // временное решение
        List<Comment> commentList = StreamSupport.stream(commentRepository.findAll().spliterator(), false)
                .filter(n -> n.getAdsEntity().getPk() == id)
                .map(commentMapper::toComment)
                .collect(Collectors.toList());
        return commentMapper.toComments(commentList);
    }

    @Override
    public Comment createComment(Authentication authentication, int id, CreateOrUpdateComment comment) {
        return null;
    }

    @Override
    public void deleteComment(Authentication authentication, int adId, int commentId) {

    }

    @Override
    public Comment updateComment(Authentication authentication, int adId, int commentId, CreateOrUpdateComment createOrUpdateComment) {
        return null;
    }
}
