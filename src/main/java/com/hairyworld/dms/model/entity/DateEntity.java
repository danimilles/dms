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
public class DateEntity implements Entity {
    private Long id;
    private Date datetimestart;
    private Date datetimeend;
    private String description;

    private Long iddog;
    private Long idservice;
    private Long idclient;
}
