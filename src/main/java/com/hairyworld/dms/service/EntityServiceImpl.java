package com.hairyworld.dms.service;

import com.hairyworld.dms.cache.CacheManager;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.repository.ClientRepositoryImpl;
import com.hairyworld.dms.repository.EntityRepository;
import com.hairyworld.dms.repository.EntityRepositoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                .forEach(clientdog -> {

            final ClientEntity clientEntity = (ClientEntity) cacheManager.get(
                    ClientEntity.builder().id(clientdog.getIdclient()).build());
            final DogEntity dogEntity = (DogEntity) cacheManager.get(
                    DogEntity.builder().id(clientdog.getIddog()).build());

            clientEntity.getDogIds().add(clientdog.getIddog());
            dogEntity.getClientIds().add(clientdog.getIdclient());
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
    }

    @Override
    public void deleteClientDogRelation(final Long idclient, final Long iddog) {
        ((ClientRepositoryImpl) entityRepositoryMap.get(EntityType.CLIENT)).deleteClientDogRelation(idclient, iddog);
        final ClientEntity client = (ClientEntity) cacheManager.get(ClientEntity.builder().id(idclient).build());
        final DogEntity dog = (DogEntity) cacheManager.get(DogEntity.builder().id(iddog).build());
        final Set<Entity> dates = cacheManager.getAllMatch(
                date -> ((DateEntity) date).isRelatedTo(iddog, EntityType.DOG) &&
                        ((DateEntity) date).isRelatedTo(idclient, EntityType.CLIENT), EntityType.DATE);
        dates.forEach(date -> {
            ((DateEntity) date).setIddog(null);
            saveEntity(date);
        });
        client.getDogIds().remove(dog.getId());
        dog.getClientIds().remove(client.getId());
    }

    @Override
    public void deleteEntity(final Entity entity) {
        deleteClientRelations(entity);
        deleteDogRelations(entity);
        deleteServiceRelations(entity);

        entityRepositoryMap.get(entity.getEntityType()).delete(entity.getId());
        cacheManager.remove(entity);
    }

    private void deleteServiceRelations(Entity entity) {
        if (entity.getEntityType().equals(EntityType.SERVICE)) {
            cacheManager.getAllMatch(date -> ((DateEntity) date).isRelatedTo(entity.getId(), EntityType.SERVICE), EntityType.DATE)
                    .forEach(date -> ((DateEntity) date).setIdservice(null));

            cacheManager.getAllMatch(payment -> ((PaymentEntity) payment).isRelatedTo(entity.getId(), EntityType.SERVICE), EntityType.PAYMENT)
                    .forEach(date -> ((PaymentEntity) date).setIdservice(null));
        }
    }

    private void deleteDogRelations(Entity entity) {
        if (entity.getEntityType().equals(EntityType.DOG)) {
            cacheManager.getAllMatch(
                    client -> ((ClientEntity) client).getDogIds().contains(entity.getId()), EntityType.CLIENT)
                    .forEach(client -> ((ClientEntity)client).getDogIds().remove(entity.getId()));

            cacheManager.getAllMatch(
                    date -> ((DateEntity) date).isRelatedTo(entity.getId(), EntityType.DOG), EntityType.DATE)
                    .forEach(date -> ((DateEntity)date).setIddog(null));
        }
    }

    private void deleteClientRelations(Entity entity) {
        if (entity.getEntityType().equals(EntityType.CLIENT)) {
            final List<Long> iddogs = ((ClientRepositoryImpl) entityRepositoryMap.get(EntityType.CLIENT))
                    .getDogToDeleteForClient(entity.getId());

            iddogs.forEach(iddog -> entityRepositoryMap.get(EntityType.DOG).delete(iddog));

            cacheManager.removeAllMatch(ent -> iddogs.contains(ent.getId()), EntityType.DOG);

            cacheManager.removeAllMatch(
                    ent -> ((DateEntity) ent).isRelatedTo(entity.getId(), EntityType.CLIENT), EntityType.DATE);

            cacheManager.getAllMatch(
                    ent -> ((PaymentEntity) ent).isRelatedTo(entity.getId(), EntityType.CLIENT), EntityType.PAYMENT)
                    .forEach(payment -> ((PaymentEntity) payment).setIdclient(null));
        }
    }
}
