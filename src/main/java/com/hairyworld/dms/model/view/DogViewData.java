package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DogViewData implements SearchTableRow {
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
    public List<TableFilter> getSearchFilters() {
        return Arrays.asList(TableFilter.DOG_NAME, TableFilter.RACE, TableFilter.OBSERVATIONS);
    }

    @Override
    public Map<String, String> getSearchColumns() {
        final Map<String, String> searchColumns = new HashMap<>();
        searchColumns.put("Id", id.toString());
        searchColumns.put("Name", name);
        searchColumns.put("Race", race);
        searchColumns.put("Observations", observations);
        return searchColumns;
    }
}
