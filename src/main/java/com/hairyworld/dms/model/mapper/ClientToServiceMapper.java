package com.hairyworld.dms.model.mapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DogClientViewData;

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
}
