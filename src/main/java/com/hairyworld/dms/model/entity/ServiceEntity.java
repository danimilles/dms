package com.hairyworld.dms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ServiceEntity implements Entity {
    private Long id;
    private String description;

    @Override
    public EntityType getEntityType() {
        return EntityType.SERVICE;
    }

}
