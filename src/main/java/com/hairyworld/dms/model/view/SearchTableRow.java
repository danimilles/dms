package com.hairyworld.dms.model.view;

import java.util.List;
import java.util.Map;

public interface SearchTableRow {
    String getIdString();

    String getName();

    List<TableFilter> getSearchFilters();

    Map<String, String> getSearchColumns();
}
