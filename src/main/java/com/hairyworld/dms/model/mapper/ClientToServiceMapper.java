package com.hairyworld.dms.model.mapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.view.clientview.ClientViewData;
import com.hairyworld.dms.model.view.clientview.DogClientViewData;
import com.hairyworld.dms.model.view.dogview.ClientDogViewData;
import com.hairyworld.dms.model.view.dogview.DogViewData;

import java.util.HashSet;
import java.util.stream.Collectors;

public class ClientToServiceMapper {
    public static ClientEntity map(final ClientViewData clientData) {
        return ClientEntity.builder()
                .id(clientData.getId())
                .name(clientData.getName())
                .observations(clientData.getObservations())
                .phone(clientData.getPhone())
                .dogIds(clientData.getDogs() == null ? new HashSet<>() :
                        clientData.getDogs().stream().map(DogClientViewData::getId).collect(Collectors.toSet()))
                .build();
    }

    public static DogEntity map(final DogViewData dogData) {
        return DogEntity.builder()
                .name(dogData.getName())
                .clientIds(dogData.getClients() == null ? new HashSet<>() :
                        dogData.getClients().stream().map(ClientDogViewData::getId).collect(Collectors.toSet()))
                .id(dogData.getId())
                .observations(dogData.getObservations())
                .race(dogData.getRace())
                .maintainment(dogData.getMaintainment())
                .image(dogData.getImage())
                .build();
    }
}
