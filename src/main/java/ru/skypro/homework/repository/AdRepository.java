package ru.skypro.homework.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.homework.entity.AdEntity;

public interface AdRepository extends CrudRepository<AdEntity,Integer> {
}
