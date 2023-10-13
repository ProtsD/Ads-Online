package ru.skypro.homework.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<CommentEntity, Integer> {
    Optional<List<CommentEntity>> findAllByAdEntityPk(Integer pk);

    @Transactional
    void deleteByAdEntityPk(Integer pk);
}
