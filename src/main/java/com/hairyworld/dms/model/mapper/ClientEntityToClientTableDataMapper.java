package com.hairyworld.dms.model.mapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.view.ClientTableData;

import java.util.Set;

public class ClientEntityToClientTableDataMapper {
    public static ClientTableData map(final ClientEntity client, final Set<Entity> dogs, final DateEntity date) {
        return ClientTableData.builder()
                .id(client.getId())
                .name(client.getName())
                .nextDate(date != null ? date.getDatetimeend() : null)
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
}
