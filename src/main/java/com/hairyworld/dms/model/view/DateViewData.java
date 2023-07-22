package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DateViewData implements ViewData {
    private Long id;
    private DateTime datetimestart;
    private DateTime datetimeend;
    private String description;

    private DogViewData dog;
    private ClientViewData client;
    private ServiceViewData service;

    @Override
    public DataType getDataType() {
        return DataType.DATE;
    }

    public boolean isRelatedTo(final Long id, final DataType type) {
        if (type == DataType.DATE) {
            return this.id.equals(id);
        } else if (type == DataType.CLIENT) {
            return client != null && client.getId().equals(id);
        } else if (type == DataType.DOG) {
            return dog != null && dog.getId().equals(id);
        } else if (type == DataType.SERVICE) {
            return service != null && service.getId().equals(id);
        } else {
            return false;
        }
    }
}
