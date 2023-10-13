package ru.skypro.homework.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.user.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUserPrincipal userDetails;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User name " + username + " not found")
                );
        userDetails.setUserDto(userMapper.toFullUserInfo(userEntity));
        return userDetails;
    }

    public boolean userExists(String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        return userEntity.isPresent();
    }

    public void createUser(Register register) {
        UserEntity userEntity = new UserEntity()
                .setUsername(register.getUsername())
                .setPassword(passwordEncoder.encode(register.getPassword()))
                .setFirstName(register.getFirstName())
                .setLastName(register.getLastName())
                .setPhone(register.getPhone())
                .setRole(register.getRole());
        userRepository.save(userEntity);
    }
}