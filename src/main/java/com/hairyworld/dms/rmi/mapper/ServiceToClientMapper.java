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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceToClientMapper {
    public static ClientViewData mapClientDataToMainView(final ClientEntity client, final Set<Entity> dogs, final DateEntity date) {
        return ClientViewData.builder()
                .id(client.getId())
                .name(client.getName())
                .dni(client.getDni())
                .nextDate(date != null ? date.getDatetimestart() : null)
                .dogs(dogs.stream().map(dog -> DogViewData.builder().name(((DogEntity) dog).getName()).id(dog.getId())
                        .race(((DogEntity) dog).getRace()).maintainment(((DogEntity) dog).getMaintainment())
                        .build()).collect(Collectors.toList()))
                .phone(client.getPhone())
                .build();
    }

    public static ClientViewData mapClientDataToClientView(final ClientEntity client, final Collection<Entity> dogs, final DateEntity date,
                                                           final Collection<Entity> dates,
                                                           final Collection<Entity> payments,
                                                           final Collection<Entity> services) {
        return ClientViewData.builder()
                .id(client.getId())
                .name(client.getName())
                .dni(client.getDni())
                .nextDate(date != null ? date.getDatetimestart() : null)
                .dogs(dogs.stream().map(dog -> DogViewData.builder()
                                .name(((DogEntity) dog).getName())
                                .id(dog.getId())
                                .race(((DogEntity) dog).getRace())
                                .maintainment(((DogEntity) dog).getMaintainment()).build())
                        .toList())
                .observations(client.getObservations())
                .dates(dates.stream().map(dateEntity -> DateViewData.builder()
                                .id(dateEntity.getId())
                                .datetimeend(((DateEntity) dateEntity).getDatetimeend())
                                .datetimestart(((DateEntity) dateEntity).getDatetimestart())
                                .description(((DateEntity) dateEntity).getDescription())
                                .dog(dogs.stream().filter(
                                        dog -> dog.getId().equals(((DateEntity) dateEntity).getIddog()))
                                        .map(dog -> DogViewData.builder().id(dog.getId()).race(((DogEntity) dog).getRace())
                                                .name(((DogEntity) dog).getName())
                                                .maintainment(((DogEntity) dog).getMaintainment()).build())
                                        .findFirst()
                                        .orElse(null))
                                .service(services.stream().filter(
                                        service -> service.getId().equals(((DateEntity) dateEntity).getIdservice()))
                                        .map(service -> ServiceViewData.builder()
                                                .id(service.getId())
                                                .description(((ServiceEntity) service).getDescription())
                                                .build())
                                        .findFirst()
                                        .orElse(null))
                                .build())
                        .toList())
                .phone(client.getPhone())
                .payments(payments.stream().map(payment -> PaymentViewData.builder()
                                .id(payment.getId())
                                .amount(((PaymentEntity) payment).getAmount())
                                .datetime(((PaymentEntity) payment).getDatetime())
                                .description(((PaymentEntity) payment).getDescription())
                                .service(services.stream().filter(
                                        service -> service.getId().equals(((PaymentEntity) payment).getIdservice()))
                                        .map(service -> ServiceViewData.builder()
                                                .id(service.getId())
                                                .description(((ServiceEntity) service).getDescription())
                                                .build())
                                        .findFirst()
                                        .orElse(null))
                                .build())
                        .toList())
                .build();
    }

    public static DogViewData mapDogDataToDogView(final DogEntity dog, final Collection<Entity> clients, final DateEntity nextDate,
                                                  final Collection<Entity> dates, final Collection<Entity> services) {
        return DogViewData.builder()
                .dates(dates.stream().map(dateEntity -> DateViewData.builder()
                                .id(dateEntity.getId())
                                .datetimeend(((DateEntity) dateEntity).getDatetimeend())
                                .datetimestart(((DateEntity) dateEntity).getDatetimestart())
                                .description(((DateEntity) dateEntity).getDescription())
                                .client(clients.stream().filter(
                                        client -> client.getId().equals(((DateEntity) dateEntity).getIdclient()))
                                        .map(client -> mapClientDataToClientViewObj((ClientEntity) client))
                                        .findFirst()
                                        .orElse(null))
                                .service(services.stream().filter(
                                        service -> service.getId().equals(((DateEntity) dateEntity).getIdservice()))
                                        .map(service -> ServiceViewData.builder()
                                                .id(service.getId())
                                                .description(((ServiceEntity) service).getDescription())
                                                .build())
                                        .findFirst()
                                        .orElse(null))
                                .build())
                        .toList())
                .clients(clients.stream().map(client -> mapClientDataToClientViewObj((ClientEntity) client)).toList())
                .nextDate(nextDate != null ? nextDate.getDatetimestart() : null)
                .maintainment(dog.getMaintainment())
                .observations(dog.getObservations())
                .name(dog.getName())
                .id(dog.getId())
                .image(dog.getImage())
                .race(dog.getRace())
                .build();
    }

    public static ClientViewData mapClientDataToClientViewObj(final ClientEntity client) {
        return ClientViewData.builder()
                .id(client.getId())
                .dni(client.getDni())
                .phone(client.getPhone())
                .name(client.getName())
                .build();
    }
}
