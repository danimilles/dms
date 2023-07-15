package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.Entity;

import java.util.Map;

public interface EntityRepository {

    Map<EntityType, Map<Long, Entity>> loadClientsAndDogs();

    Map<Long, Entity> loadDates();

    Map<Long, Entity> loadPayments();

    Map<Long, Entity> loadServices();

    Long saveEntity(Entity entity, EntityType entityType);

    void deleteEntity(Long id, EntityType entityType);

    void saveClientDogRelation(Long idclient, Long iddog);
}
