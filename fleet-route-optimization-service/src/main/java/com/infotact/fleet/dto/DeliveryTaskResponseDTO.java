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
	
    @Schema(description = "Unique primary sequence identifier tracking the task record inside the system data tier", example = "10025")
	private Long id;

    @Schema(description = "Complete destination street address registered for the task", example = "123 Logistics Blvd, Suite 400, Hinjavadi")
    private String destinationAddress;

    @Schema(description = "Geographic latitude value matching the target address drop-off coordinate", example = "18.5204")
    private Double latitude;

    @Schema(description = "Geographic longitude value matching the target address drop-off coordinate", example = "73.8567")
    private Double longitude;

    @Schema(description = "Total physical mass weight of the package in kilograms (kg)", example = "450.75")
    private Double packageWeightKg;

    @Schema(description = "Evaluated state tracking tag displaying the step lifecycle of the processing task", example = "ASSIGNED")
    private TaskStatus status; 

    @Schema(description = "Unique identifier of the calculated microservice route execution timeline this task belongs to", example = "5008")
    private Long assignedRouteId;

    @Schema(description = "System timestamp registering precisely when the initial task ledger entry was created", example = "2026-07-17T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "System timestamp marking the absolute latest update adjustment applied to the task records", example = "2026-07-17T15:30:00")
    private LocalDateTime updatedAt;

}