package com.hairyworld.dms.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ServiceViewData implements ViewData {
    private Long id;
    private String description;

    @Override
    public DataType getDataType() {
        return DataType.SERVICE;
    }
}
