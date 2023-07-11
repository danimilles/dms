package com.hairyworld.dms.cache;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.util.EntityType;

import java.util.Map;

public interface CacheManager {
    void putEntityIntoCache(Entity entity, EntityType entityType);

    void putEntityCache(Map<Long, Entity> newCache, EntityType entityType);

    Entity getEntityFromCache(Long id, EntityType entityType);
}
