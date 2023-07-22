package com.hairyworld.dms.rmi.mapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.model.entity.ServiceEntity;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.PaymentViewData;
import com.hairyworld.dms.model.view.ServiceViewData;

import java.util.HashSet;
import java.util.stream.Collectors;

public class ClientToServiceMapper {

    private ClientToServiceMapper() {
    }

    public static ClientEntity mapClientDataToClientEntity(final ClientViewData clientData) {
        return ClientEntity.builder()
                .id(clientData.getId())
                .name(clientData.getName())
                .dni(clientData.getDni())
                .observations(clientData.getObservations())
                .phone(clientData.getPhone())
                .dogIds(clientData.getDogs() == null ? new HashSet<>() :
                        clientData.getDogs().stream().map(DogViewData::getId).collect(Collectors.toSet()))
                .build();
    }

    public static DogEntity mapDogDataToDogEntity(final DogViewData dogData) {
        return DogEntity.builder()
                .name(dogData.getName())
                .clientIds(dogData.getClients() == null ? new HashSet<>() :
                        dogData.getClients().stream().map(ClientViewData::getId).collect(Collectors.toSet()))
                .id(dogData.getId())
                .observations(dogData.getObservations())
                .race(dogData.getRace())
                .maintainment(dogData.getMaintainment())
                .image(dogData.getImage())
                .build();
    }

    public static Entity mapDateDataToDateEntity(final DateViewData dateViewData) {
        return DateEntity.builder()
                .id(dateViewData.getId())
                .datetimestart(dateViewData.getDatetimestart())
                .description(dateViewData.getDescription())
                .datetimeend(dateViewData.getDatetimeend())
                .iddog(dateViewData.getDog() != null ? dateViewData.getDog().getId() : null)
                .idclient(dateViewData.getClient() != null ? dateViewData.getClient().getId() : null)
                .idservice(dateViewData.getService() != null ? dateViewData.getService().getId() : null)
                .build();
    }

    public static Entity mapServiceDataToServiceEntity(final ServiceViewData serviceViewData) {
        return ServiceEntity.builder()
                .id(serviceViewData.getId())
                .description(serviceViewData.getDescription())
                .build();
    }

    public static Entity mapPaymentDataToPaymentEntity(final PaymentViewData paymentViewData) {
        return PaymentEntity.builder()
                .id(paymentViewData.getId())
                .amount(paymentViewData.getAmount())
                .datetime(paymentViewData.getDatetime())
                .idclient(paymentViewData.getClient() != null ? paymentViewData.getClient().getId() : null)
                .idservice(paymentViewData.getService() != null ? paymentViewData.getService().getId() : null)
                .build();
    }
}
