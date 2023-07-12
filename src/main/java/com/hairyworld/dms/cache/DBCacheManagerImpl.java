package com.hairyworld.dms.cache;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.Entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        if (newCache != null) {
            cache.put(entityType, new DBCache(newCache));
        }
    }

    @Override
    public Collection<Entity> getAllEntityFromCache(final EntityType entityType) {
        return cache.get(entityType).getAll();
    }

    @Override
    public Entity getEntityFromCache(final Long id, final EntityType entityType) {
        return cache.get(entityType).get(id);
    }

    @Override
    public Set<Entity> getAllMAtchEntityFromCache(final Predicate<Entity> filter, final EntityType entityType) {
        return cache.get(entityType).getAll().stream().filter(filter).collect(Collectors.toSet());
    }

    @Override
    public void removeEntityFromCache(Long id, EntityType entityType) {
        cache.get(entityType).remove(id);
    }

    @Override
    public void removeAllMatchEntityFromCache(Predicate<Entity> filter, EntityType entityType) {
        cache.get(entityType).getAll().stream().filter(filter).forEach(entity ->
                cache.get(entityType).remove(entity.getId()));
    }
}
