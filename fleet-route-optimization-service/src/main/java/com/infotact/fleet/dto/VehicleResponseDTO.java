package com.infotact.fleet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private Long id;
    private String licensePlate;
    private Double capacityKg;
    private String fuelType;
    private Boolean maintenanceStatus;
    private Long assignedDriverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}