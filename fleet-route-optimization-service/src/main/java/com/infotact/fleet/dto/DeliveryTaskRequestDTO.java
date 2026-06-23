package com.infotact.fleet.dto;

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
public class DeliveryTaskRequestDTO {
	
	@NotBlank(message = "Destination address field description is required")
	private String destinationAddress;
	
	@NotNull(message = "Latitude coordinate value is required")
    @Min(value = -90, message = "Latitude must be greater than or equal to -90")
    @Max(value = 90, message = "Latitude must be less than or equal to 90")
    private Double latitude;
	
	@NotNull(message = "Longitude coordinate value is required")
    @Min(value = -180, message = "Longitude must be greater than or equal to -180")
    @Max(value = 180, message = "Longitude must be less than or equal to 180")
    private Double longitude;
	
	@NotNull(message = "Package weight specification is required")
    @Positive(message = "Package weight must be a positive number greater than zero")
    private Double packageWeightKg;

}
