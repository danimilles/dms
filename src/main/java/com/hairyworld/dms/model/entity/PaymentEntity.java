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
public class PaymentEntity implements Entity {
    private Long id;
    private DateTime datetime;
    private String description;

    private Long idservice;
    private Long idclient;
}
