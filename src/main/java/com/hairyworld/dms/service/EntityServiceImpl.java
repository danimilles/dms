package com.hairyworld.dms.service;

import com.hairyworld.dms.cache.CacheManager;
import com.hairyworld.dms.cache.DBCacheManagerImpl;
import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.EntityRepositoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;

@Service
public class EntityServiceImpl implements EntityService {

    private static final Logger LOGGER = LogManager.getLogger(EntityServiceImpl.class);

    private CacheManager cacheManager;
    private final EntityRepositoryImpl cacheRepository;

    public EntityServiceImpl(final EntityRepositoryImpl cacheRepository) {
        this.cacheRepository = cacheRepository;
        this.cacheManager = new DBCacheManagerImpl();
        reloadCache();
    }

    @Override
    public void reloadCache() {
        LOGGER.info("Loading DB in memory...");
        cacheManager.putEntityCache(cacheRepository.loadClientsAndDogs().get(EntityType.CLIENT), EntityType.CLIENT);
        cacheManager.putEntityCache(cacheRepository.loadClientsAndDogs().get(EntityType.DOG), EntityType.DOG);
        cacheManager.putEntityCache(cacheRepository.loadDates(), EntityType.DATE);
        cacheManager.putEntityCache(cacheRepository.loadServices(), EntityType.SERVICE);
        cacheManager.putEntityCache(cacheRepository.loadPayments(), EntityType.PAYMENT);
        LOGGER.info("Load successful");
    }

    @Override
    public Collection<Entity> getAll(final EntityType entityType) {
        return cacheManager.getAllEntityFromCache(entityType);
    }

    @Override
    public Entity get(Long id, EntityType entityType) {
        return cacheManager.getEntityFromCache(id, entityType);
    }

    @Override
    public Collection<Entity> getAllMatch(final Predicate<Entity> filter, final EntityType entityType) {
        return cacheManager.getAllEntityFromCacheMatchs(filter, entityType);
    }

    @Override
    public DateEntity getNextDateForClient(final Long idClient) {
        return (DateEntity) cacheManager.getAllEntityFromCacheMatchs(
                entity -> ((DateEntity) entity).getIdclient().equals(idClient), EntityType.DATE)
                .stream()
                .filter(entity -> ((DateEntity) entity).getDatetimestart().getMillis() >= System.currentTimeMillis())
                .min(Comparator.comparing(date -> ((DateEntity) date).getDatetimestart()))
                .orElse(null);
    }
}
