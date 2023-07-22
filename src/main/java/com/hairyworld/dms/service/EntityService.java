package com.hairyworld.dms.service;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;

import java.util.Collection;
import java.util.function.Predicate;

public interface EntityService {
    void reloadCache();

    Collection<Entity> getAllEntites(EntityType entityType);

    Entity getEntity(Entity entity);

    Collection<Entity> getAllEntitiesMatch(Predicate<Entity> filter, EntityType entityType);

    Long saveEntity(Entity entity);

    void saveClientDogRelation(Long idclient, Long iddog);

    void deleteClientDogRelation(Long idclient, Long iddog);

    void deleteEntity(Entity id);
}
