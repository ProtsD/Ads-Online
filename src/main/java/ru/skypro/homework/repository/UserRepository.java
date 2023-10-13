package ru.skypro.homework.repository;


import org.springframework.data.repository.CrudRepository;
import ru.skypro.homework.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
}
