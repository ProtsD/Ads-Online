package ru.skypro.homework.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.user.Register;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

@Service
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUserPrincipal userDetails;

    public SecurityUserDetailsService(UserRepository userRepository,
                                      UserMapper userMapper,
                                      SecurityUserPrincipal userDetails) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userDetails = userDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User userDto = userRepository.findByUsername(username)
                .map(userMapper::toUser)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User name "+username+" not found")
                );
        userDetails.setUserDto(userDto);
        return userDetails;
    }

    public boolean userExists(String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        return userEntity.isPresent();
    }

    public void createUser(Register register) {
        UserEntity userEntity = new UserEntity()
                .setUsername(register.getUsername())
                .setPassword(register.getPassword())
                .setFirstName(register.getFirstName())
                .setLastName(register.getLastName())
                .setPhone(register.getPhone())
                .setRole(register.getRole());
        userRepository.save(userEntity);
    }
}