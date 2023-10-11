package ru.skypro.homework.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<CommentEntity, Integer> {
    Optional<List<CommentEntity>> findAllByAdEntityPk(Integer pk);
    void deleteByAdEntityPk(Integer pk);
}
