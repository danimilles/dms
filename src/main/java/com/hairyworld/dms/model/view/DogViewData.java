package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DogViewData {
    private Long id;
    private String name;
    private String maintainment;
    private String race;
    private String observations;
    private byte[] image;
    private DateTime nextDate;

    private List<ClientViewData> clients;
    private List<DateViewData> dates;
}
