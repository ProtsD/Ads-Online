package ru.skypro.homework.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.homework.entity.AdsEntity;

public interface AdsRepository extends CrudRepository<AdsEntity,Integer> {
}
