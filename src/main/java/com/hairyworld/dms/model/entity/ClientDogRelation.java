package com.hairyworld.dms.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClientDogRelation {
    private Long idclient;
    private Long iddog;
}
