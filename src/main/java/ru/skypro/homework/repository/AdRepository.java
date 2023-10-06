package ru.skypro.homework.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.homework.entity.AdEntity;

import java.util.List;
import java.util.Optional;

public interface AdRepository extends CrudRepository<AdEntity, Integer> {
    Optional<List<AdEntity>> findAllByAuthorId(Integer id);
}
