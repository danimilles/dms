package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.Entity;

import java.util.Map;

public interface EntityRepository {

    EntityType getEntityType();

    Map<Long, Entity> loadAll();

    Long save(Entity entity);

    void delete(Long id);

}
