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
public class DateViewData {
    private Long id;
    private DateTime datetimestart;
    private DateTime datetimeend;
    private String description;

    private DogViewData dog;
    private ClientViewData client;
    private ServiceViewData service;
}
