package ru.skypro.homework.service.util;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.security.SecurityUserPrincipal;

public class ServiceUtils {
    public static User getCurrentUser(Authentication authentication) {
        return ((SecurityUserPrincipal) authentication.getPrincipal()).getUserDto();
    }
}
