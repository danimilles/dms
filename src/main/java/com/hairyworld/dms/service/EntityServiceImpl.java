package com.hairyworld.dms.service;

import com.hairyworld.dms.cache.CacheManager;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.ClientRepositoryImpl;
import com.hairyworld.dms.repository.EntityRepository;
import com.hairyworld.dms.repository.EntityRepositoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class EntityServiceImpl implements EntityService {

    private static final Logger LOGGER = LogManager.getLogger(EntityServiceImpl.class);

    private final CacheManager cacheManager;
    private final Map<EntityType, EntityRepository> entityRepositoryMap;

    public EntityServiceImpl(final ApplicationContext context, final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.entityRepositoryMap = new EnumMap<>(EntityType.class);
        context.getBeansOfType(EntityRepositoryImpl.class).values()
                .forEach(entityRepositoryImpl ->
                    entityRepositoryMap.put(entityRepositoryImpl.getEntityType(),
                            entityRepositoryImpl)
                );
        reloadCache();
    }

    @Override
    public void reloadCache() {
        LOGGER.info("Loading DB in memory...");

        entityRepositoryMap.values().forEach(entityRepository ->
                cacheManager.putCache(entityRepository.loadAll(), entityRepository.getEntityType()));

        ((ClientRepositoryImpl) entityRepositoryMap.get(EntityType.CLIENT)).loadAllClientAndDogRelations()
                .forEach((key, value) -> {
            final ClientEntity clientEntity = (ClientEntity) cacheManager.get(ClientEntity.builder().id(key).build());
            final DogEntity dogEntity = (DogEntity) cacheManager.get(DogEntity.builder().id(key).build());
            clientEntity.getDogIds().add(key);
            dogEntity.getClientIds().add(value);
        });

        LOGGER.info("Load successful");
    }

    @Override
    public Collection<Entity> getAllEntites(final EntityType entityType) {
        return cacheManager.getAll(entityType);
    }

    @Override
    public Entity getEntity(final Entity entity) {
        return cacheManager.get(entity);
    }

    @Override
    public Collection<Entity> getAllEntitiesMatch(final Predicate<Entity> filter, final EntityType entityType) {
        return cacheManager.getAllMatch(filter, entityType);
    }

    @Override
    public DateEntity getNextDate(final Collection<Entity> dates) {
        return (DateEntity) dates
                .stream()
                .filter(entity -> ((DateEntity) entity).getDatetimestart().getMillis() >= System.currentTimeMillis())
                .min(Comparator.comparing(date -> ((DateEntity) date).getDatetimestart()))
                .orElse(null);
    }

    @Override
    public Long saveEntity(final Entity entity) {
        entity.setId(entityRepositoryMap.get(entity.getEntityType()).save(entity));
        cacheManager.put(entity);
        return entity.getId();
    }

    @Override
    public void saveClientDogRelation(final Long idclient, final Long iddog) {
        ((ClientRepositoryImpl) entityRepositoryMap.get(EntityType.CLIENT)).saveClientDogRelation(idclient, iddog);
        final ClientEntity client = (ClientEntity) cacheManager.get(ClientEntity.builder().id(idclient).build());
        final DogEntity dog = (DogEntity) cacheManager.get(DogEntity.builder().id(iddog).build());
        client.getDogIds().add(dog.getId());
        dog.getClientIds().add(client.getId());
        cacheManager.put(client);
        cacheManager.put(dog);
    }

    @Override
    public void deleteEntity(final Entity entity) {
        if (entity.getEntityType().equals(EntityType.CLIENT)) {
            final List<Long> iddogs = ((ClientRepositoryImpl) entityRepositoryMap.get(EntityType.CLIENT)).getDogToDeleteForClient(entity.getId());
            iddogs.forEach(iddog -> entityRepositoryMap.get(EntityType.DOG).delete(iddog));
            cacheManager.removeAllMatch(ent -> iddogs.contains(ent.getId()), EntityType.DOG);
        }

        if (entity.getEntityType().equals(EntityType.DOG)) {
            cacheManager.getAllMatch(client -> ((ClientEntity) client).getDogIds().contains(entity.getId()), EntityType.CLIENT)
                    .forEach(client -> ((ClientEntity)client).getDogIds().remove(entity.getId()));
        }

        entityRepositoryMap.get(entity.getEntityType()).delete(entity.getId());
        cacheManager.remove(entity);
    }
}
