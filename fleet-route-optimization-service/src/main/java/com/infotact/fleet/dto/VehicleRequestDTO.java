package com.infotact.fleet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {

    @NotBlank(message = "License plate cannot be blank")
    @Size(min = 4, max = 20, message = "License plate must be between 4 and 20 characters")
    private String licensePlate;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    private Double capacityKg;

    @NotBlank(message = "Fuel type cannot be blank")
    private String fuelType;
}