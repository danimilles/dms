package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.util.EntityType;

import java.util.Map;

public interface CacheRepository {

    Map<EntityType, Map<Long, Entity>> loadClientsAndDogs();

    Map<Long, Entity> loadDates();

    Map<Long, Entity> loadPayments();

    Map<Long, Entity> loadServices();
}
