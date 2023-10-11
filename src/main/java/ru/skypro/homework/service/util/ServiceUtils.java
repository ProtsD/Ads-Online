package ru.skypro.homework.service.util;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.user.FullUserInfo;
import ru.skypro.homework.security.SecurityUserPrincipal;

@Component
public class ServiceUtils {
    public FullUserInfo getCurrentUser(Authentication authentication) {
        return ((SecurityUserPrincipal) authentication.getPrincipal()).getUserDto();
    }
}
