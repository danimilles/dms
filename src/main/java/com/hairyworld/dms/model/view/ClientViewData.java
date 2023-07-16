package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClientViewData {
    private Long id;
    private String name;
    private String phone;
    private String dni;
    private String observations;

    private DateTime nextDate;
    private List<DogViewData> dogs;
    private List<DateViewData> dates;
    private List<PaymentViewData> payments;

    public String getDogsString() {
        return dogs.stream().map(dog -> dog.getName() + " -> " + dog.getRace())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse(Strings.EMPTY);
    }

    public String getMantainment() {
        return dogs.stream().map(dog -> dog.getName() + " -> " + dog.getMaintainment())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse(Strings.EMPTY);
    }
}
