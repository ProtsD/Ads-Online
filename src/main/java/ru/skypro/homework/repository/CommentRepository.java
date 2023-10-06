package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.skypro.homework.entity.CommentEntity;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<CommentEntity, Integer> {
    @Query(value = "SELECT * from comment where comment.ad_pk=:adId", nativeQuery = true)
    Optional<CommentEntity> getAllCommentForAd(@Param("adId") int adId);
}
