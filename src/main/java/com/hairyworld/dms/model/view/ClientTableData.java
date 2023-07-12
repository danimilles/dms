package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;


@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ClientTableData {
    private Long id;
    private String name;
    private String phone;
    private String dogs;
    private DateTime nextDate;
    private String mantainment;
}
