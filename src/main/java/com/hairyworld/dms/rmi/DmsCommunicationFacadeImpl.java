package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.model.mapper.ClientToServiceMapper;
import com.hairyworld.dms.model.mapper.ServiceToClientMapper;
import com.hairyworld.dms.model.view.dogview.ClientDogViewData;
import com.hairyworld.dms.model.view.dogview.DogViewData;
import com.hairyworld.dms.model.view.mainview.ClientTableData;
import com.hairyworld.dms.model.view.clientview.ClientViewData;
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
    public List<ClientTableData> getClientTableData() {
        final List<ClientTableData> clientTableData = new ArrayList<>();
        final Collection<Entity> clients = entityService.getAll(EntityType.CLIENT);

        for (final Entity entry : clients) {
            final Set<Entity> dogs =
                    ((ClientEntity) entry).getDogIds().stream().map(iddog -> entityService.get(iddog, EntityType.DOG))
                            .collect(Collectors.toSet());

            final DateEntity nextDate = entityService.getNextDate(entityService.getAllMatch(
                    entity -> ((DateEntity) entity).getIdclient().equals(entry.getId()), EntityType.DATE));

            clientTableData.add(ServiceToClientMapper.map((ClientEntity) entry, dogs, nextDate));
        }

        return clientTableData;
    }

    @Override
    public ClientViewData getClientViewData(final Long clientId) {
        final ClientEntity client = (ClientEntity) entityService.get(clientId, EntityType.CLIENT);

        if (client == null) {
            LOGGER.error("Client with id {} not found", clientId);
            return null;
        }

        final Collection<Entity> dates = entityService.getAllMatch(
                entity -> ((DateEntity) entity).getIdclient().equals(clientId), EntityType.DATE);
        final DateEntity nextDate = entityService.getNextDate(dates);
        final Collection<Entity> payments  = entityService.getAllMatch(payment -> ((PaymentEntity) payment).getIdclient().equals(clientId), EntityType.PAYMENT);
        final Collection<Entity> dogs  = entityService.getAllMatch(dog -> ((DogEntity) dog).getClientIds().contains(clientId), EntityType.DOG);
        final Collection<Entity> services = entityService.getAll(EntityType.SERVICE);

        return ServiceToClientMapper.map(client, dogs, nextDate, dates, payments, services);
    }

    @Override
    public void saveClient(final ClientViewData clientViewData) {
        entityService.saveEntity(ClientToServiceMapper.map(clientViewData), EntityType.CLIENT);
    }

    @Override
    public void deleteClient(final Long id) {
        entityService.deleteEntity(id, EntityType.CLIENT);
    }

    @Override
    public void saveDog(final DogViewData dogViewData) {
        dogViewData.setId(entityService.saveEntity(ClientToServiceMapper.map(dogViewData), EntityType.DOG));
        dogViewData.getClients().forEach(x -> entityService.saveClientDogRelation(x.getId(), dogViewData.getId()));
    }

    @Override
    public void deleteDog(final Long id) {

    }

    @Override
    public DogViewData getDogData(final Long dogId) {
        final DogEntity dog = (DogEntity) entityService.get(dogId, EntityType.DOG);

        if (dog == null) {
            LOGGER.error("Dog with id {} not found", dogId);
            return null;
        }

        final Collection<Entity> dates = entityService.getAllMatch(
                entity -> ((DateEntity) entity).getIddog().equals(dogId), EntityType.DATE);
        final DateEntity nextDate = entityService.getNextDate(dates);
        final Collection<Entity> clients  =
                entityService.getAllMatch(client -> ((ClientEntity) client).getDogIds().contains(dogId), EntityType.CLIENT);
        final Collection<Entity> services = entityService.getAll(EntityType.SERVICE);

        return ServiceToClientMapper.map(dog, clients, nextDate, dates, services);
    }

    @Override
    public ClientDogViewData getClientDogViewData(final Long clientId) {
        return ServiceToClientMapper.map((ClientEntity) entityService.get(clientId, EntityType.CLIENT));
    }

}
