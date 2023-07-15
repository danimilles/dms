package com.hairyworld.dms.model.view.clientview;

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
public class ClientViewData {
    private Long id;
    private String name;
    private String phone;
    private String dni;
    private DateTime nextDate;
    private String observations;
    private List<DogClientViewData> dogs;
    private List<DateClientViewData> dates;
    private List<PaymentClientViewData> payments;
}
