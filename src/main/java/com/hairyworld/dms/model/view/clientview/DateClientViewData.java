package com.hairyworld.dms.model.view.clientview;

import com.hairyworld.dms.model.view.ServiceEntityViewData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;


@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class DateClientViewData {
    private Long id;
    private String dogName;
    private String description;
    private ServiceEntityViewData service;
    private DateTime datetimeend;
    private DateTime datetimestart;
}
