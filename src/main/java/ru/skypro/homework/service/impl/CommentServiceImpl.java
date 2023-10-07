package ru.skypro.homework.service.impl;

import liquibase.pro.packaged.A;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.SecurityUserPrincipal;
import ru.skypro.homework.service.CommentService;

import java.util.stream.Collectors;

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
    public Comment createComment(Authentication authentication, int id, CreateOrUpdateComment createOrUpdateComment) {
        CommentEntity result = commentMapper.toNewEntity(createOrUpdateComment,userRepository.findById(getCurrentUser(authentication).getId()).orElseThrow(),adRepository.findById(id).orElseThrow());
        result=commentRepository.save(result);
        return commentMapper.toComment(result);
    }

    @Override
    public void deleteComment(Authentication authentication, int adId, int commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow();
        if (checkUser(authentication,adId,commentEntity)){
            commentRepository.deleteById(commentId);
        }
        else {
        }
    }

    @Override
    public Comment updateComment(Authentication authentication, int adId, int commentId, CreateOrUpdateComment createOrUpdateComment) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow();
        if (checkUser(authentication,adId,commentEntity)){
            commentEntity.setText(createOrUpdateComment.getText());
            Comment result = commentMapper.toComment(commentRepository.save(commentEntity));
            return result;
        }
        else{
            return null;
        }
    }
    private boolean checkUser(Authentication authentication,int adId,CommentEntity commentEntity){
        if (getCurrentUser(authentication).getId()==commentEntity.getAuthor().getId() & commentEntity.getAdsEntity().getPk()==adId){
            return true;
        }
        else if (commentEntity.getAdsEntity().getPk()==adId) {
            return false;
        }
        else {
            throw new ForbiddenException("");
        }
    }
    private User getCurrentUser(Authentication authentication) {
        return ((SecurityUserPrincipal) authentication.getPrincipal()).getUserDto();
    }
}
