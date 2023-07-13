package com.hairyworld.dms.cache;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface CacheManager {
    void putEntityIntoCache(Entity entity, EntityType entityType);

    void putEntityCache(Map<Long, Entity> newCache, EntityType entityType);

    Collection<Entity> getAllEntityFromCache(EntityType entityType);

    Entity getEntityFromCache(Long id, EntityType entityType);

    Set<Entity> getAllMatchEntityFromCache(Predicate<Entity> filter, EntityType entityType);

    void removeEntityFromCache(Long id, EntityType entityType);
    void removeAllMatchEntityFromCache(Predicate<Entity> filter, EntityType entityType);
}
