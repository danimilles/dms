package com.hairyworld.dms.model.mapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.model.entity.ServiceEntity;
import com.hairyworld.dms.model.view.dogview.ClientDogViewData;
import com.hairyworld.dms.model.view.dogview.DateDogViewData;
import com.hairyworld.dms.model.view.dogview.DogViewData;
import com.hairyworld.dms.model.view.mainview.ClientTableData;
import com.hairyworld.dms.model.view.clientview.ClientViewData;
import com.hairyworld.dms.model.view.clientview.DateClientViewData;
import com.hairyworld.dms.model.view.clientview.DogClientViewData;
import com.hairyworld.dms.model.view.clientview.PaymentClientViewData;
import com.hairyworld.dms.model.view.ServiceEntityViewData;
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.Set;

public class ServiceToClientMapper {
    public static ClientTableData map(final ClientEntity client, final Set<Entity> dogs, final DateEntity date) {
        return ClientTableData.builder()
                .id(client.getId())
                .name(client.getName())
                .nextDate(date != null ? date.getDatetimestart() : null)
                .dogs(dogs.stream().map(dog -> ((DogEntity) dog).getName() + " : " + ((DogEntity) dog).getRace())
                        .reduce((a, b) -> a + ",\n" + b)
                        .orElse(""))
                .mantainment(dogs.stream()
                        .filter(dog -> !Strings.isEmpty(((DogEntity) dog).getMaintainment()))
                        .map(dog -> ((DogEntity) dog).getName() + " : " + ((DogEntity) dog).getMaintainment())
                        .reduce((a, b) -> a + ",\n" + b).orElse(Strings.EMPTY))
                .phone(client.getPhone())
                .build();
    }

    public static ClientViewData map(final ClientEntity client, final Collection<Entity> dogs, final DateEntity date,
                                     final Collection<Entity> dates,
                                     final Collection<Entity> payments,
                                     final Collection<Entity> services) {
        return ClientViewData.builder()
                .id(client.getId())
                .name(client.getName())
                .nextDate(date != null ? date.getDatetimestart() : null)
                .dogs(dogs.stream().map(dog -> DogClientViewData.builder()
                                .name(((DogEntity) dog).getName())
                                .id(dog.getId())
                                .race(((DogEntity) dog).getRace())
                                .maintainment(((DogEntity) dog).getMaintainment()).build())
                        .toList())
                .observations(client.getObservations())
                .dates(dates.stream().map(dateEntity -> DateClientViewData.builder()
                                .id(dateEntity.getId())
                                .datetimeend(((DateEntity) dateEntity).getDatetimeend())
                                .datetimestart(((DateEntity) dateEntity).getDatetimestart())
                                .description(((DateEntity) dateEntity).getDescription())
                                .dogName(dogs.stream().filter(
                                        dog -> dog.getId().equals(((DateEntity) dateEntity).getIddog()))
                                        .map(dog -> ((DogEntity) dog).getName())
                                        .findFirst()
                                        .orElse(null))
                                .service(services.stream().filter(
                                        service -> service.getId().equals(((DateEntity) dateEntity).getIdservice()))
                                        .map(service -> ServiceEntityViewData.builder()
                                                .id(service.getId())
                                                .description(((ServiceEntity) service).getDescription())
                                                .build())
                                        .findFirst()
                                        .orElse(null))
                                .build())
                        .toList())
                .phone(client.getPhone())
                .payments(payments.stream().map(payment -> PaymentClientViewData.builder()
                                .id(payment.getId())
                                .amount(((PaymentEntity) payment).getAmount())
                                .datetime(((PaymentEntity) payment).getDatetime())
                                .description(((PaymentEntity) payment).getDescription())
                                .service(services.stream().filter(
                                        service -> service.getId().equals(((PaymentEntity) payment).getIdservice()))
                                        .map(service -> ServiceEntityViewData.builder()
                                                .id(service.getId())
                                                .description(((ServiceEntity) service).getDescription())
                                                .build())
                                        .findFirst()
                                        .orElse(null))
                                .build())
                        .toList())
                .build();
    }

    public static DogViewData map(final DogEntity dog, final Collection<Entity> clients, final DateEntity nextDate,
                                  final Collection<Entity> dates, final Collection<Entity> services) {
        return DogViewData.builder()
                .dates(dates.stream().map(dateEntity -> DateDogViewData.builder()
                                .id(dateEntity.getId())
                                .datetimeend(((DateEntity) dateEntity).getDatetimeend())
                                .datetimestart(((DateEntity) dateEntity).getDatetimestart())
                                .description(((DateEntity) dateEntity).getDescription())
                                .clientName(clients.stream().filter(
                                        client -> client.getId().equals(((DateEntity) dateEntity).getIdclient()))
                                        .map(client -> ((ClientEntity) client).getName())
                                        .findFirst()
                                        .orElse(null))
                                .service(services.stream().filter(
                                        service -> service.getId().equals(((DateEntity) dateEntity).getIdservice()))
                                        .map(service -> ServiceEntityViewData.builder()
                                                .id(service.getId())
                                                .description(((ServiceEntity) service).getDescription())
                                                .build())
                                        .findFirst()
                                        .orElse(null))
                                .build())
                        .toList())
                .clients(clients.stream().map(client -> map((ClientEntity) client)).toList())
                .nextDate(nextDate != null ? nextDate.getDatetimestart() : null)
                .maintainment(dog.getMaintainment())
                .observations(dog.getObservations())
                .name(dog.getName())
                .id(dog.getId())
                .image(dog.getImage())
                .race(dog.getRace())
                .build();
    }

    public static ClientDogViewData map(final ClientEntity client) {
        return ClientDogViewData.builder()
                .id(client.getId())
                .phone(client.getPhone())
                .name(client.getName())
                .build();
    }
}
