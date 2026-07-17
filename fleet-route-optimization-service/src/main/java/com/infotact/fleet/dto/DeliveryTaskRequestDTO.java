package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "DeliveryTaskRequest",
        description = "Payload structure used to instantiate or update warehouse staging tasks"
)
public class DeliveryTaskRequestDTO {

    @Schema(
        description = "Complete physical street address location for dispatch delivery drop-off",
        example = "123 Logistics Blvd, Suite 400, Hinjavadi",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
	@NotBlank(message = "Destination address field description is required")
	private String destinationAddress;
	
    @Schema(
        description = "Geographic latitude coordinate matching the WGS 84 system boundary",
        example = "18.5204",
        minimum = "-90",
        maximum = "90",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
	@NotNull(message = "Latitude coordinate value is required")
    @Min(value = -90, message = "Latitude must be greater than or equal to -90")
    @Max(value = 90, message = "Latitude must be less than or equal to 90")
    private Double latitude;
	
    @Schema(
        description = "Geographic longitude coordinate matching the WGS 84 system boundary",
        example = "73.8567",
        minimum = "-180",
        maximum = "180",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
	@NotNull(message = "Longitude coordinate value is required")
    @Min(value = -180, message = "Longitude must be greater than or equal to -180")
    @Max(value = 180, message = "Longitude must be less than or equal to 180")
    private Double longitude;
	
    @Schema(
        description = "Total physical payload mass weight specification restricted to kilograms (kg)",
        example = "450.75",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
	@NotNull(message = "Package weight specification is required")
    @Positive(message = "Package weight must be a positive number greater than zero")
    private Double packageWeightKg;

}