package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.util.ServiceUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final AdRepository adRepository;
    private final ServiceUtils serviceUtils;

    @Override
    public Comments getAllComments(Authentication authentication, Integer id) {
        List<Comment> comments = commentRepository.findAllByAdEntityPk(id)
                .orElseThrow()
                .stream()
                .map(commentMapper::toComment)
                .collect(Collectors.toList());

        if (comments.isEmpty()) throw new NotFoundException("Comments for Ad with id="+id+" doesn't found.");

        return commentMapper.toComments(comments);
    }

    @Override
    public Comment createComment(Authentication authentication, Integer id, CreateOrUpdateComment createOrUpdateComment) {
        CommentEntity result = commentMapper.toNewEntity(
                createOrUpdateComment,
                userMapper.toEntity(serviceUtils.getCurrentUser(authentication)),
                adRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment with id="+id+" doesn't found."))
        );

        result = commentRepository.save(result);

        return commentMapper.toComment(result);
    }

    @Override
    public void deleteComment(Authentication authentication, Integer adId, Integer commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new NotFoundException("Comment with id="+commentId+" doesn't found.");
        }
    }

    @Override
    public Comment updateComment(Authentication authentication, Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id="+commentId+" doesn't found."));

        commentEntity.setText(createOrUpdateComment.getText());
        commentEntity = commentRepository.save(commentEntity);

        return commentMapper.toComment(commentEntity);
    }
}
