package com.hairyworld.dms.model.view.dogview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ClientDogViewData {
    private Long id;
    private String name;
    private String phone;
    private String dni;
}
