package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.Entity;

import java.util.Map;

public interface EntityRepository {

    Map<EntityType, Map<Long, Entity>> loadClientsAndDogs();

    Map<Long, Entity> loadDates();

    Map<Long, Entity> loadPayments();

    Map<Long, Entity> loadServices();
}
