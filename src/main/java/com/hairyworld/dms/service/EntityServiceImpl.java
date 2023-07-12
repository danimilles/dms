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
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
public class EntityServiceImpl implements EntityService {

    private static final Logger LOGGER = LogManager.getLogger(EntityServiceImpl.class);

    private CacheManager cacheManager;
    private final EntityRepositoryImpl entityRepository;

    public EntityServiceImpl(final EntityRepositoryImpl entityRepository) {
        this.entityRepository = entityRepository;
        this.cacheManager = new DBCacheManagerImpl();
        reloadCache();
    }

    @Override
    public void reloadCache() {
        LOGGER.info("Loading DB in memory...");
        cacheManager.putEntityCache(entityRepository.loadClientsAndDogs().get(EntityType.CLIENT), EntityType.CLIENT);
        cacheManager.putEntityCache(entityRepository.loadClientsAndDogs().get(EntityType.DOG), EntityType.DOG);
        cacheManager.putEntityCache(entityRepository.loadDates(), EntityType.DATE);
        cacheManager.putEntityCache(entityRepository.loadServices(), EntityType.SERVICE);
        cacheManager.putEntityCache(entityRepository.loadPayments(), EntityType.PAYMENT);
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
        return cacheManager.getAllMAtchEntityFromCache(filter, entityType);
    }

    @Override
    public DateEntity getNextDateForClient(final Long idClient) {
        return (DateEntity) cacheManager.getAllMAtchEntityFromCache(
                entity -> ((DateEntity) entity).getIdclient().equals(idClient), EntityType.DATE)
                .stream()
                .filter(entity -> ((DateEntity) entity).getDatetimestart().getMillis() >= System.currentTimeMillis())
                .min(Comparator.comparing(date -> ((DateEntity) date).getDatetimestart()))
                .orElse(null);
    }

    @Override
    public void saveEntity(final Entity entity, final EntityType entityType) {
        entity.setId(entityRepository.saveEntity(entity, entityType));
        cacheManager.putEntityIntoCache(entity, entityType);
    }

    @Override
    public void deleteEntity(final Long id, final EntityType entityType) {
        final List<Long> iddogs = entityRepository.deleteEntity(id, entityType);
        cacheManager.removeEntityFromCache(id, entityType);
        if (!CollectionUtils.isEmpty(iddogs) && EntityType.CLIENT.equals(entityType)) {
            cacheManager.removeAllMatchEntityFromCache(entity -> iddogs.contains(entity.getId()), entityType);
        }
    }
}
