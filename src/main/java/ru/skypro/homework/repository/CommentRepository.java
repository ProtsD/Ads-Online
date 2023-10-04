package ru.skypro.homework.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.homework.entity.CommentEntity;

import javax.persistence.criteria.CriteriaBuilder;

public interface CommentRepository extends CrudRepository<CommentEntity, Integer> {
}
