package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.math.BigDecimal;


@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class PaymentClientViewData {
    private Long id;
    private ServiceClientViewData service;
    private String description;
    private BigDecimal amount;
    private DateTime datetime;
}
