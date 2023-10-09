package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.dto.user.Role;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.exception.ForbiddenException;
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

    @Override
    public Comments getAllComments(Authentication authentication, int id) {
        List<Comment> comments = commentRepository.findAllByAdEntityPk(id)
                .orElseThrow(NotFoundException::new)
                .stream()
                .map(commentMapper::toComment)
                .collect(Collectors.toList());

        return commentMapper.toComments(comments);
    }

    @Override
    public Comment createComment(Authentication authentication, int id, CreateOrUpdateComment createOrUpdateComment) {
        CommentEntity result = commentMapper.toNewEntity(
                createOrUpdateComment,
                userMapper.toEntity(ServiceUtils.getCurrentUser(authentication)),
                adRepository.findById(id).orElseThrow(() -> new NotFoundException(""))
        );

        result = commentRepository.save(result);

        return commentMapper.toComment(result);
    }

    @Override
    public void deleteComment(Authentication authentication, int adId, int commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(""));

        if (checkUser(authentication, adId, commentEntity)) {
            commentRepository.deleteById(commentId);
        }
    }

    @Override
    public Comment updateComment(Authentication authentication, int adId, int commentId, CreateOrUpdateComment createOrUpdateComment) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(""));

        if (checkUser(authentication, adId, commentEntity)) {
            commentEntity.setText(createOrUpdateComment.getText());
            commentEntity = commentRepository.save(commentEntity);
        }

        return commentMapper.toComment(commentEntity);
    }

    private boolean checkUser(Authentication authentication, int adId, CommentEntity commentEntity) {
        if (commentEntity.getAdEntity().getPk() != adId) {
            throw new NotFoundException("");
        } else if (ServiceUtils.getCurrentUser(authentication).getId() == commentEntity.getAuthor().getId() || ServiceUtils.getCurrentUser(authentication).getRole().equals(Role.ADMIN)) {
            return true;
        } else {
            throw new ForbiddenException("");
        }
    }
}
