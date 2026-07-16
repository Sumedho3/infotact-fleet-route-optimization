package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "VehicleResponse",
        description = "Operational description matrix detailing physical vehicle metadata"
)

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