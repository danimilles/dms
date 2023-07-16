package com.hairyworld.dms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClientEntity implements Entity {
    private Long id;
    private String name;
    private String phone;
    private String dni;
    private String observations;

    private Set<Long> dogIds;

    @Override
    public EntityType getEntityType() {
        return EntityType.CLIENT;
    }
}
