package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import com.infotact.fleet.model.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "DeliveryTaskResponse",
        description = "Output registry schema mapping finalized geospatial stop data points"
)

public class DeliveryTaskResponseDTO {
	
	private Long id;
    private String destinationAddress;
    private Double latitude;
    private Double longitude;
    private Double packageWeightKg;
    private TaskStatus status; // Evaluated state enum tracking tag
    private Long assignedRouteId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
