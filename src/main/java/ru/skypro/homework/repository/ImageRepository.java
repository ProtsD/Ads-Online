package ru.skypro.homework.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.homework.entity.ImageEntity;

public interface ImageRepository extends CrudRepository<ImageEntity,Integer> {
}
