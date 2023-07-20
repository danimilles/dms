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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class ServiceToClientMapper {
    public static ClientViewData mapClientDataToMainView(final ClientEntity client, final Set<Entity> dogs, final DateEntity date) {
        return ClientViewData.builder()
                .id(client.getId())
                .name(client.getName())
                .dni(client.getDni())
                .nextDate(date != null ? date.getDatetimestart() : null)
                .dogs(dogs.stream().map(dog -> DogViewData.builder().name(((DogEntity) dog).getName()).id(dog.getId())
                        .race(((DogEntity) dog).getRace()).maintainment(((DogEntity) dog).getMaintainment())
                        .build()).toList())
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
                .dates(new ArrayList<>())
                .dogs(new ArrayList<>())
                .payments(new ArrayList<>())
                .observations(client.getObservations())
                .build();
    }

    public static DogViewData mapDogDataToDogViewObj(final DogEntity dog) {
        return DogViewData.builder()
                .id(dog.getId())
                .race(dog.getRace())
                .observations(dog.getObservations())
                .maintainment(dog.getMaintainment())
                .name(dog.getName())
                .dates(new ArrayList<>())
                .clients(new ArrayList<>())
                .image(dog.getImage())
                .build();
    }

    public static ServiceViewData mapServiceDataToDogViewObj(final ServiceEntity service) {
        return ServiceViewData.builder()
                .id(service.getId())
                .description(service.getDescription())
                .build();
    }

    public static DateViewData mapDateToMainViewObj(final Entity date, final Collection<Entity> dogs,
                                                 final Collection<Entity> clients, final Collection<Entity> services) {
        return DateViewData.builder()
                .client(clients.iterator().hasNext() ? mapClientDataToClientViewObj((ClientEntity) clients.iterator().next()) : null)
                .dog(dogs.iterator().hasNext() ? mapDogDataToDogViewObj((DogEntity) dogs.iterator().next()) : null)
                .service(services.iterator().hasNext() ? mapServiceDataToDogViewObj((ServiceEntity) services.iterator().next()) : null)
                .id(date.getId())
                .description(((DateEntity) date).getDescription())
                .datetimestart(((DateEntity) date).getDatetimestart())
                .datetimeend(((DateEntity) date).getDatetimeend())
                .build();
    }

    public static DateViewData mapDateToMainViewObj(final Entity entry, final DogEntity dog,
                                                    final ClientEntity client, final ServiceEntity service) {
        return DateViewData.builder()
                .client(client != null ? mapClientDataToClientViewObj(client) : null)
                .dog(dog != null ? mapDogDataToDogViewObj(dog) : null)
                .service(service != null ? mapServiceDataToDogViewObj(service) : null)
                .id(entry.getId())
                .description(((DateEntity) entry).getDescription())
                .datetimestart(((DateEntity) entry).getDatetimestart())
                .datetimeend(((DateEntity) entry).getDatetimeend())
                .build();
    }
}