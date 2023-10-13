package ru.skypro.homework.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.user.FullUserInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class SecurityUserPrincipal implements UserDetails {
    private FullUserInfo userDto;


    public SecurityUserPrincipal(FullUserInfo userDto) {
        this.userDto = userDto;
    }

    public void setUserDto(FullUserInfo userDto) {
        this.userDto = userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(userDto)
                .map(FullUserInfo::getRole)
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.name()))
                .map(Collections::singleton)
                .orElseGet(Collections::emptySet);
    }

    @Override
    public String getPassword() {
        return Optional.ofNullable(userDto)
                .map(FullUserInfo::getPassword)
                .orElse(null);
    }

    @Override
    public String getUsername() {
        return Optional.ofNullable(userDto)
                .map(FullUserInfo::getUsername)
                .orElse(null);
    }

    public FullUserInfo getUserDto() {
        return userDto;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}