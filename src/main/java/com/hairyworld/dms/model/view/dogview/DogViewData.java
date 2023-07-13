package com.hairyworld.dms.model.view.dogview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class DogViewData {
    private Long id;
    private String name;
    private String race;
    private byte[] image;
    private String observations;
    private String maintainment;
    private DateTime nextDate;
    private List<ClientDogViewData> clients;
    private List<DateDogViewData> dates;
}
