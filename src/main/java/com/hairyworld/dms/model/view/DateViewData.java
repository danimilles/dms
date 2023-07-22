package com.hairyworld.dms.model.view;

import com.hairyworld.dms.model.entity.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DateViewData {
    private Long id;
    private DateTime datetimestart;
    private DateTime datetimeend;
    private String description;

    private DogViewData dog;
    private ClientViewData client;
    private ServiceViewData service;

    public boolean isRelatedTo(final Long id, final EntityType type) {
        if (type == EntityType.CLIENT) {
            return this.id.equals(id);
        } else if (type == EntityType.CLIENT) {
            return client != null && client.getId().equals(id);
        } else if (type == EntityType.DOG) {
            return dog != null && dog.getId().equals(id);
        } else if (type == EntityType.SERVICE) {
            return service != null && service.getId().equals(id);
        } else {
            return false;
        }
    }
}
