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
}
