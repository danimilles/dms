package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.model.entity.ServiceEntity;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.PaymentViewData;
import com.hairyworld.dms.model.view.SearchTableRow;
import com.hairyworld.dms.model.view.ServiceViewData;
import com.hairyworld.dms.rmi.mapper.ClientToServiceMapper;
import com.hairyworld.dms.rmi.mapper.ServiceToClientMapper;
import com.hairyworld.dms.service.EntityService;
import com.hairyworld.dms.service.EntityServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

            final DateEntity nextDate = ServiceToClientMapper.getNextDate(entityService.getAllEntitiesMatch(
                    entity -> entry.getId().equals(((DateEntity) entity).getIdclient()), EntityType.DATE));

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
                entity -> clientId.equals(((DateEntity) entity).getIdclient()), EntityType.DATE);
        final DateEntity nextDate = ServiceToClientMapper.getNextDate(dates);
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
    public void saveClientDog(final Long idclient, final Long iddog) {
        entityService.saveClientDogRelation(idclient, iddog);
    }

    @Override
    public void deleteClientDog(final Long idclient, final Long iddog) {
        entityService.deleteClientDogRelation(idclient, iddog);
    }
    @Override
    public List<DateViewData> getDateCalendarData() {
        final Collection<Entity> dates = entityService.getAllEntites(EntityType.DATE);
        final List<DateViewData> dateViewData = new ArrayList<>();
        for (final Entity entry : dates) {
            final DogEntity dog = (DogEntity)
                    entityService.getAllEntitiesMatch(x -> x.getId().equals(((DateEntity) entry).getIddog()),
                    EntityType.DOG).stream().findFirst().orElse(null);
            final ClientEntity client = (ClientEntity)
                    entityService.getAllEntitiesMatch(x -> x.getId().equals(((DateEntity) entry).getIdclient()),
                    EntityType.CLIENT).stream().findFirst().orElse(null);
            final ServiceEntity service = (ServiceEntity)
                    entityService.getAllEntitiesMatch(x -> x.getId().equals(((DateEntity) entry).getIdservice()),
                    EntityType.SERVICE).stream().findFirst().orElse(null);

            dateViewData.add(ServiceToClientMapper.mapDateToMainViewObj(entry, dog, client, service));
        }

        return dateViewData;
    }

    @Override
    public void deleteClient(final Long id) {
        entityService.deleteEntity(ClientEntity.builder().id(id).build());
    }

    @Override
    public DogViewData getDogViewData(final Long dogId) {
        final DogEntity dog = (DogEntity) entityService.getEntity(DogEntity.builder().id(dogId).build());

        if (dog == null) {
            LOGGER.error("Dog with id {} not found", dogId);
            return null;
        }

        final Collection<Entity> dates = entityService.getAllEntitiesMatch(
                entity -> dogId.equals(((DateEntity) entity).getIddog()), EntityType.DATE);
        final DateEntity nextDate = ServiceToClientMapper.getNextDate(dates);
        final Collection<Entity> clients  =
                entityService.getAllEntitiesMatch(client -> ((ClientEntity) client).getDogIds().contains(dogId), EntityType.CLIENT);
        final Collection<Entity> services = entityService.getAllEntites(EntityType.SERVICE);

        return ServiceToClientMapper.mapDogDataToDogView(dog, clients, nextDate, dates, services);
    }

    @Override
    public void saveDog(final DogViewData dogViewData) {
        dogViewData.setId(entityService.saveEntity(ClientToServiceMapper.mapDogDataToDogEntity(dogViewData)));
    }

    @Override
    public void deleteDog(final Long id) {
        entityService.deleteEntity(DogEntity.builder().id(id).build());
    }

    @Override
    public Long saveDate(final DateViewData dateViewData) {
        return entityService.saveEntity(ClientToServiceMapper.mapDateDataToDateEntity(dateViewData));
    }

    @Override
    public void deleteDate(final DateViewData dateViewData) {
        entityService.deleteEntity(ClientToServiceMapper.mapDateDataToDateEntity(dateViewData));
    }

    @Override
    public List<ServiceViewData> getServiceViewTableData() {
        return entityService.getAllEntites(EntityType.SERVICE).stream()
                .map(x -> ServiceToClientMapper.mapServiceDataToServiceViewObj((ServiceEntity) x))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceViewData getServiceViewData(final Long serviceId) {
        return ServiceToClientMapper.mapServiceDataToServiceViewObj((ServiceEntity) entityService.getEntity(ServiceEntity.builder().id(serviceId).build()));
    }

    @Override
    public void deleteService(final ServiceViewData serviceViewData) {
        entityService.deleteEntity(ClientToServiceMapper.mapServiceDataToServiceEntity(serviceViewData));
    }

    @Override
    public void saveService(final ServiceViewData serviceViewData) {
        entityService.saveEntity(ClientToServiceMapper.mapServiceDataToServiceEntity(serviceViewData));
    }

    @Override
    public PaymentViewData getPaymentViewData(final Long paymentId) {
        final PaymentEntity payment = (PaymentEntity) entityService.getEntity(PaymentEntity.builder().id(paymentId).build());
        final ClientEntity client = (ClientEntity) entityService.getEntity(ClientEntity.builder().id(payment.getIdclient()).build());
        final ServiceEntity service = (ServiceEntity) entityService.getEntity(ServiceEntity.builder().id(payment.getIdservice()).build());
        return ServiceToClientMapper.mapPaymentDataToPaymentView(payment, service, client);
    }

    @Override
    public void deletePayment(final PaymentViewData paymentViewData) {
        entityService.deleteEntity(ClientToServiceMapper.mapPaymentDataToPaymentEntity(paymentViewData));
    }

    @Override
    public void savePayment(final PaymentViewData paymentViewData) {
        entityService.saveEntity(ClientToServiceMapper.mapPaymentDataToPaymentEntity(paymentViewData));
    }

    @Override
    public List<SearchTableRow> getSearchTableData(final SearchTableRow searchTableRow) {
        if (searchTableRow instanceof DogViewData dogViewData) {
            return entityService.getAllEntites(EntityType.CLIENT).stream()
                    .filter(x -> dogViewData.getId() == null || !((ClientEntity) x).getDogIds().contains(dogViewData.getId()))
                    .map(x -> ServiceToClientMapper.mapClientDataToClientViewObj((ClientEntity) x))
                    .collect(Collectors.toList());
        } else if (searchTableRow instanceof ClientViewData clientViewData) {
            return entityService.getAllEntites(EntityType.DOG).stream()
                    .filter(x -> clientViewData.getId() == null || !((DogEntity) x).getClientIds().contains(clientViewData.getId()))
                    .map(x -> ServiceToClientMapper.mapDogDataToDogViewObj((DogEntity) x))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
