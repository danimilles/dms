package com.hairyworld.dms.cache;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.util.EntityType;

import java.util.HashMap;
import java.util.Map;

public class DBCacheManagerImpl implements CacheManager {

    private Map<EntityType, DBCache> cache;

    public DBCacheManagerImpl() {
        cache = new HashMap<>();
    }

    @Override
    public void putEntityIntoCache(final Entity entity, final EntityType entityType) {
        if (entity != null) {
            cache.get(entityType).put(entity);
        }
    }

    @Override
    public void putEntityCache(final Map<Long, Entity> newCache, final EntityType entityType) {
        if (newCache != null && !newCache.isEmpty()) {
            cache.put(entityType, new DBCache(newCache));
        }
    }

    @Override
    public Entity getEntityFromCache(final Long id, final EntityType entityType) {
        if (id != null) {
            return cache.get(entityType).get(id);
        }

        return null;
    }
}
