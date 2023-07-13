package com.hairyworld.dms.service;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.Entity;

import java.util.Collection;
import java.util.function.Predicate;

public interface EntityService {
    void reloadCache();

    Collection<Entity> getAll(EntityType entityType);

    Entity get(Long id, EntityType entityType);

    Collection<Entity> getAllMatch(Predicate<Entity> filter, EntityType entityType);

    DateEntity getNextDate(Collection<Entity> dates);

    Long saveEntity(Entity entity, EntityType entityType);

    void saveClientDogRelation(Long idclient, Long iddog);

    void deleteEntity(Long id, EntityType entityType);
}
