package com.hairyworld.dms.cache;

import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.Entity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class DBCacheManagerImpl implements CacheManager {

    private final Map<EntityType, DBCache> cache;

    public DBCacheManagerImpl() {
        cache = new EnumMap<>(EntityType.class);
    }

    @Override
    public void put(final Entity entity) {
        if (entity != null) {
            cache.get(entity.getEntityType()).put(entity);
        }
    }

    @Override
    public void putCache(final Map<Long, Entity> newCache, final EntityType entityType) {
        if (newCache != null) {
            cache.put(entityType, new DBCache(newCache));
        }
    }

    @Override
    public Collection<Entity> getAll(final EntityType entityType) {
        return cache.get(entityType).getAll();
    }

    @Override
    public Entity get(final Entity entity) {
        return cache.get(entity.getEntityType()).get(entity.getId());
    }

    @Override
    public Set<Entity> getAllMatch(final Predicate<Entity> filter, final EntityType entityType) {
        return cache.get(entityType)
                .getAll().stream().filter(filter).collect(Collectors.toSet());
    }

    @Override
    public void remove(final Entity entity) {
        cache.get(entity.getEntityType()).remove(entity.getId());
    }

    @Override
    public void removeAllMatch(final Predicate<Entity> filter, final EntityType entityType) {
        this.getAllMatch(filter, entityType).forEach(this::remove);
    }
}
