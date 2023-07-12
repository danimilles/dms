package com.hairyworld.dms.model.mapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.model.entity.ServiceEntity;
import com.hairyworld.dms.model.view.ClientTableData;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DateClientViewData;
import com.hairyworld.dms.model.view.DogClientViewData;
import com.hairyworld.dms.model.view.PaymentClientViewData;

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
                        .filter(dog -> ((DogEntity) dog).getMaintainment() != null)
                        .map(dog -> ((DogEntity) dog).getName() + " : " + ((DogEntity) dog).getMaintainment())
                        .reduce((a, b) -> a + ",\n" + b).orElse(""))
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
                                .race(((DogEntity) dog).getRace()).build())
                        .toList())
                .observations(client.getObservations())
                .dates(dates.stream().map(dateEntity -> DateClientViewData.builder()
                                .id(dateEntity.getId())
                                .datetimeend(((DateEntity) dateEntity).getDatetimestart())
                                .datetimestart(((DateEntity) dateEntity).getDatetimestart())
                                .description(((DateEntity) dateEntity).getDescription())
                                .service(services.stream().filter(
                                        service -> service.getId().equals(((DateEntity) dateEntity).getIdservice()))
                                        .map(service -> ((ServiceEntity) service).getDescription())
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
                                        .map(service -> ((ServiceEntity) service).getDescription())
                                        .findFirst()
                                        .orElse(null))
                                .build())
                        .toList())
                .build();
    }
}
