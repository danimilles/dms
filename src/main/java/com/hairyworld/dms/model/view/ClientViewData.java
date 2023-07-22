package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClientViewData implements SearchTableRow, ViewData {
    private Long id;
    private String name;
    private String phone;
    private String dni;
    private String observations;

    private DateTime nextDate;
    private List<DogViewData> dogs;
    private List<DateViewData> dates;
    private List<PaymentViewData> payments;

    @Override
    public DataType getDataType() {
        return DataType.CLIENT;
    }

    public String getDogsString() {
        return dogs.stream().map(dog -> dog.getName() + " -> " + dog.getRace())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse(Strings.EMPTY);
    }

    public String getMantainment() {
        return dogs.stream().filter(x -> !Strings.isEmpty(x.getMaintainment()))
                .map(dog -> dog.getName() + " -> " + dog.getMaintainment())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse(Strings.EMPTY);
    }

    @Override
    public List<TableFilter> getSearchFilters() {
        return Arrays.asList(TableFilter.CLIENT_NAME, TableFilter.PHONE, TableFilter.DNI);
    }

    @Override
    public Map<String, String> getSearchColumns() {
        final Map<String, String> searchColumns = new HashMap<>();
        searchColumns.put("Id", id.toString());
        searchColumns.put("Nombre", name);
        searchColumns.put("Telefono", phone);
        searchColumns.put("Dni", dni);
        return searchColumns;
    }

    @Override
    public String getIdString() {
        return id.toString();
    }
}
