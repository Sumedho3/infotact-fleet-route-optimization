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

    @Schema(description = "Unique sequence identifier tracking the vehicle record inside the system database layer", example = "45")
    private Long id;

    @Schema(description = "Official tracking license number plate sequence mounted on the physical asset frame", example = "MH-12-QW-5678")
    private String licensePlate;

    @Schema(description = "Maximum supported mass weight capacity parameter metrics handled by the freight container layer, scaled in kilograms (kg)", example = "3500.00")
    private Double capacityKg;

    @Schema(description = "Designated physical fuel or propulsion framework type required by the motorized unit layer instance", example = "DIESEL")
    private String fuelType;

    @Schema(description = "Flag tracking the operational mechanical availability state of the freight asset (true indicates it is currently undergoing servicing)", example = "false")
    private Boolean maintenanceStatus;

    @Schema(description = "Unique sequence identifier corresponding to the personnel operator profile actively linked to this vehicle frame context", example = "301")
    private Long assignedDriverId;

    @Schema(description = "System isolation creation timestamp tracing the deployment enrollment origin of the profile", example = "2026-02-18T11:20:00")
    private LocalDateTime createdAt;

    @Schema(description = "System adjustment timestamp marking the latest configuration shift applied to the vehicle records", example = "2026-07-17T15:36:00")
    private LocalDateTime updatedAt;
}