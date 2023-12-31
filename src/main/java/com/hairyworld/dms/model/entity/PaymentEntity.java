package com.hairyworld.dms.model.entity;

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
public class PaymentEntity implements Entity {
    private Long id;
    private DateTime datetime;
    private String description;
    private BigDecimal amount;

    private Long idservice;
    private Long idclient;

    @Override
    public EntityType getEntityType() {
        return EntityType.PAYMENT;
    }

    public boolean isRelatedTo(final Long id, final EntityType type) {
        if (type == EntityType.PAYMENT) {
            return id.equals(idclient);
        } else if (type == EntityType.CLIENT) {
            return id.equals(idclient);
        } else if (type == EntityType.SERVICE) {
            return id.equals(idservice);
        } else {
            return false;
        }
    }
}
