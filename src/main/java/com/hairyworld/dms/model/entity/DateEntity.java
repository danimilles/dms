package com.hairyworld.dms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DateEntity implements Entity {
    private Long id;
    private DateTime datetimestart;
    private DateTime datetimeend;
    private String description;

    private Long iddog;
    private Long idservice;
    private Long idclient;

    @Override
    public EntityType getEntityType() {
        return EntityType.DATE;
    }

    public boolean isRelatedTo(final Long id, final EntityType type) {
        if (type == EntityType.DATE) {
            return this.id.equals(id);
        } else if (type == EntityType.CLIENT) {
            return id.equals(idclient);
        } else if (type == EntityType.DOG) {
            return id.equals(idclient);
        } else if (type == EntityType.SERVICE) {
            return id.equals(idservice);
        } else {
            return false;
        }
    }
}
