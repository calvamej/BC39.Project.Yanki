package com.bootcamp.project.yanki.repository;

import com.bootcamp.project.yanki.entity.YankiEntity;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface YankiRepository extends ReactiveCrudRepository<YankiEntity, ObjectId> {
}
