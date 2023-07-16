package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.rmi.mapper.ClientToServiceMapper;
import com.hairyworld.dms.rmi.mapper.ServiceToClientMapper;
import com.hairyworld.dms.service.EntityService;
import com.hairyworld.dms.service.EntityServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DmsCommunicationFacadeImpl implements DmsCommunicationFacade {
    private static final Logger LOGGER = LogManager.getLogger(DmsCommunicationFacadeImpl.class);

    private final EntityService entityService;

    public DmsCommunicationFacadeImpl(final EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    @Override
    public List<ClientViewData> getClientTableData() {
        final List<ClientViewData> clientTableData = new ArrayList<>();
        final Collection<Entity> clients = entityService.getAllEntites(EntityType.CLIENT);

        for (final Entity entry : clients) {
            final Set<Entity> dogs =
                    ((ClientEntity) entry).getDogIds().stream().map(iddog -> entityService.getEntity(
                            DogEntity.builder().id(iddog).build()))
                            .collect(Collectors.toSet());

            final DateEntity nextDate = entityService.getNextDate(entityService.getAllEntitiesMatch(
                    entity -> ((DateEntity) entity).getIdclient().equals(entry.getId()), EntityType.DATE));

            clientTableData.add(ServiceToClientMapper.mapClientDataToMainView((ClientEntity) entry, dogs, nextDate));
        }

        return clientTableData;
    }

    @Override
    public ClientViewData getClientViewData(final Long clientId) {
        final ClientEntity client = (ClientEntity) entityService.getEntity(ClientEntity.builder().id(clientId).build());

        if (client == null) {
            LOGGER.error("Client with id {} not found", clientId);
            return null;
        }

        final Collection<Entity> dates = entityService.getAllEntitiesMatch(
                entity -> ((DateEntity) entity).getIdclient().equals(clientId), EntityType.DATE);
        final DateEntity nextDate = entityService.getNextDate(dates);
        final Collection<Entity> payments  = entityService.getAllEntitiesMatch(payment -> ((PaymentEntity) payment).getIdclient().equals(clientId), EntityType.PAYMENT);
        final Collection<Entity> dogs  = entityService.getAllEntitiesMatch(dog -> ((DogEntity) dog).getClientIds().contains(clientId), EntityType.DOG);
        final Collection<Entity> services = entityService.getAllEntites(EntityType.SERVICE);

        return ServiceToClientMapper.mapClientDataToClientView(client, dogs, nextDate, dates, payments, services);
    }

    @Override
    public void saveClient(final ClientViewData clientViewData) {
        entityService.saveEntity(ClientToServiceMapper.mapClientDataToClientEntity(clientViewData));
    }

    @Override
    public void deleteClient(final Long id) {
        entityService.deleteEntity(ClientEntity.builder().id(id).build());
    }

    @Override
    public void saveDog(final DogViewData dogViewData) {
        dogViewData.setId(entityService.saveEntity(ClientToServiceMapper.mapDogDataToDogEntity(dogViewData)));
        dogViewData.getClients().forEach(x -> entityService.saveClientDogRelation(x.getId(), dogViewData.getId()));
    }

    @Override
    public void deleteDog(final Long id) {
        entityService.deleteEntity(DogEntity.builder().id(id).build());
    }

    @Override
    public DogViewData getDogData(final Long dogId) {
        final DogEntity dog = (DogEntity) entityService.getEntity(DogEntity.builder().id(dogId).build());

        if (dog == null) {
            LOGGER.error("Dog with id {} not found", dogId);
            return null;
        }

        final Collection<Entity> dates = entityService.getAllEntitiesMatch(
                entity -> ((DateEntity) entity).getIddog().equals(dogId), EntityType.DATE);
        final DateEntity nextDate = entityService.getNextDate(dates);
        final Collection<Entity> clients  =
                entityService.getAllEntitiesMatch(client -> ((ClientEntity) client).getDogIds().contains(dogId), EntityType.CLIENT);
        final Collection<Entity> services = entityService.getAllEntites(EntityType.SERVICE);

        return ServiceToClientMapper.mapDogDataToDogView(dog, clients, nextDate, dates, services);
    }

    @Override
    public ClientViewData getClientDogViewData(final Long clientId) {
        return ServiceToClientMapper
                .mapClientDataToClientViewObj((ClientEntity) entityService.getEntity(
                        ClientEntity.builder().id(clientId).build()));
    }

}
