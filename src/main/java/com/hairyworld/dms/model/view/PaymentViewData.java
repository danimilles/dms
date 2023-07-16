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
public class PaymentViewData {
    private Long id;
    private DateTime datetime;
    private String description;
    private BigDecimal amount;

    private ServiceViewData service;
    private ClientViewData client;
}
