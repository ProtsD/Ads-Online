package ru.skypro.homework.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.user.FullUserInfo;
import ru.skypro.homework.dto.user.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.util.ServiceUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecurityAnnotationMethods {
    private final ServiceUtils serviceUtils;
    private final AdRepository adRepository;
    private final CommentRepository commentRepository;

    public boolean hasPermission(Authentication authentication, Integer id) {
        FullUserInfo currentUser = serviceUtils.getCurrentUser(authentication);
        AdEntity currentAd = adRepository.findById(id).orElseThrow(NotFoundException::new);

        return currentUser.getId() == currentAd.getAuthor().getId() || currentUser.getRole().equals(Role.ADMIN);
    }

    public boolean hasPermission(Authentication authentication, Integer adId, Integer commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(NotFoundException::new);

        if (!Objects.equals(commentEntity.getAdEntity().getPk(), adId)) {
            throw new NotFoundException("");
        } else return serviceUtils.getCurrentUser(authentication).getId() == commentEntity.getAuthor().getId() || serviceUtils.getCurrentUser(authentication).getRole().equals(Role.ADMIN);
    }
}
