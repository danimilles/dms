package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentViewData implements ViewData {
    private Long id;
    private DateTime datetime;
    private String description;
    private BigDecimal amount;

    private ServiceViewData service;
    private ClientViewData client;


    @Override
    public DataType getDataType() {
        return DataType.PAYMENT;
    }

    public boolean isRelatedTo(final Long id, final DataType type) {
        if (type == DataType.PAYMENT) {
            return this.id.equals(id);
        } else if (type == DataType.CLIENT) {
            return client != null && client.getId().equals(id);
        } else if (type == DataType.SERVICE) {
            return service != null && service.getId().equals(id);
        } else {
            return false;
        }
    }

    public String getClientString() {
        return client != null ? client.getName() : null;
    }

    public String getServiceString() {
        return service != null ? service.getDescription() : null;
    }
}
