package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ClientTableData {
    private Long id;
    private String name;
    private Integer phone;
    private String dogs;
    private Date nextDate;
    private String mantainment;
}
