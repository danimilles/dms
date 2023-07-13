package com.hairyworld.dms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DogEntity implements Entity {
    private Long id;
    private String name;
    private String maintainment;
    private String race;
    private String observations;
    private byte[] image;
    
    private Set<Long> clientIds;
}
