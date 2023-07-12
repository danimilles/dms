package com.hairyworld.dms.cache;

import com.hairyworld.dms.model.entity.Entity;

import java.util.Collection;
import java.util.Map;

public class DBCache {

    private final Map<Long, Entity> cacheMap;

    public DBCache(final Map<Long, Entity> cacheMap) {
        this.cacheMap = cacheMap;
    }

    public void put(final Entity entity) {
        if (entity != null) {
            cacheMap.put(entity.getId(), entity);
        }
    }

    public Entity get(final Long id) {
        if (id != null) {
            return cacheMap.get(id);
        }

        return null;
    }

    public Collection<Entity> getAll() {
        return cacheMap.values();
    }

    public void remove(final Long id) {
        if (id != null) {
            cacheMap.remove(id);
        }
    }
}
