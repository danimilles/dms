package com.hairyworld.dms.model.mapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.view.ClientViewData;

import java.util.HashSet;

public class ClientToServiceMapper {
    public static ClientEntity map(final ClientViewData clientData) {
        return ClientEntity.builder()
                .id(clientData.getId())
                .name(clientData.getName())
                .observations(clientData.getObservations())
                .phone(clientData.getPhone())
                .dogIds(new HashSet<>())
                .build();
    }
}
