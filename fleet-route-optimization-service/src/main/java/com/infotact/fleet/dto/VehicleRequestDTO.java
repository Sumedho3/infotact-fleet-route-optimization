package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "VehicleRequest",
        description = "Operational description matrix detailing physical vehicle metadata"
)
public class VehicleRequestDTO {

    @Schema(
        description = "Official tracking license number plate sequence mounted on the physical asset frame", 
        example = "MH-12-QW-5678", 
        minLength = 4, 
        maxLength = 20, 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "License plate cannot be blank")
    @Size(min = 4, max = 20, message = "License plate must be between 4 and 20 characters")
    private String licensePlate;

    @Schema(
        description = "Maximum supported mass weight capacity parameter profile metrics handled by the freight container layer, scaled in kilograms (kg)", 
        example = "3500.00", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    private Double capacityKg;

    @Schema(
        description = "Designated physical fuel or propulsion framework type required by the motorized unit layer instance", 
        example = "DIESEL", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Fuel type cannot be blank")
    private String fuelType;
}