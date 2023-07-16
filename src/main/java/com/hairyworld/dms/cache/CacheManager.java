package com.hairyworld.dms.cache;

import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface CacheManager {
    void put(Entity entity);

    void putCache(Map<Long, Entity> newCache, EntityType entityType);

    Collection<Entity> getAll(EntityType entityType);

    Entity get(Entity entity);

    Set<Entity> getAllMatch(Predicate<Entity> filter, EntityType entityType);

    void remove(Entity entity);

    void removeAllMatch(Predicate<Entity> filter, EntityType entityType);
}
