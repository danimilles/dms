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
public class DogViewData implements SearchTableRow, ViewData {
    private Long id;
    private String name;
    private String maintainment;
    private String race;
    private String observations;
    private byte[] image;
    private DateTime nextDate;

    private List<ClientViewData> clients;
    private List<DateViewData> dates;

    @Override
    public DataType getDataType() {
        return DataType.DOG;
    }

    @Override
    public List<TableFilter> getSearchFilters() {
        return Arrays.asList(TableFilter.DOG_NAME, TableFilter.RACE, TableFilter.OBSERVATIONS);
    }

    @Override
    public Map<String, String> getSearchColumns() {
        final Map<String, String> searchColumns = new HashMap<>();
        searchColumns.put("Id", id.toString());
        searchColumns.put("Nombre", name);
        searchColumns.put("Raza", race);
        searchColumns.put("Observaciones", observations);
        return searchColumns;
    }

    @Override
    public String getIdString() {
        return id.toString();
    }

    public String getClientsString() {
        return clients.stream().map(ClientViewData::getName)
                .reduce((a, b) -> a + ",\n" + b)
                .orElse(Strings.EMPTY);
    }

}
