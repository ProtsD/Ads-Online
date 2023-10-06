package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdRepository adRepository;

    @Override
    public Comments getAllComments(Authentication authentication, int id) {
        return commentMapper.toComments(commentRepository.getAllCommentForAd(id).stream()
                .map(commentMapper::toComment)
                .collect(Collectors.toList()));
    }

    @Override
    public Comment createComment(Authentication authentication, int id, CreateOrUpdateComment comment) {
        int currentId = ((UserEntity) authentication.getPrincipal()).getId();
        CommentEntity result = commentMapper.toNewEntity(comment,userRepository.findById(currentId).orElse(new UserEntity()),adRepository.findById(id).orElse(new AdEntity()));
        commentRepository.save(result);
        return commentMapper.toComment(result);
    }

    @Override
    public void deleteComment(Authentication authentication, int adId, int commentId) {

    }

    @Override
    public Comment updateComment(Authentication authentication, int adId, int commentId, CreateOrUpdateComment createOrUpdateComment) {
        return null;
    }
}
