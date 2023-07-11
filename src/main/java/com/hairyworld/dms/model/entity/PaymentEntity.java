package com.hairyworld.dms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentEntity implements Entity {
    private Long id;
    private Date datetime;
    private String description;

    private Long idservice;
    private Long idclient;
}
