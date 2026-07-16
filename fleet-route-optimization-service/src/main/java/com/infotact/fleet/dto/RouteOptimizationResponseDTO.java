package com.infotact.fleet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "RouteOptimizationResponse",
        description = "Output response payload containing computed metrics and sorted stop paths"
)

public class RouteOptimizationResponseDTO {

    private Long vehicleId;
    private String vehicleLicensePlate;
    
    // The final algorithmically sorted sequence of tasks to follow
    private List<DeliveryTaskResponseDTO> optimizedStops;
    
    private Double totalDistanceKm;
    private Double totalDurationMinutes;
}