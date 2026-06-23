package com.infotact.fleet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {
    private String licensePlate;
    private Double capacityKg;
    private String fuelType;
}