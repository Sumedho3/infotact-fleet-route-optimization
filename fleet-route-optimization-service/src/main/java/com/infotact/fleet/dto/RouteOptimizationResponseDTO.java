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

    @Schema(description = "Unique tracking sequence identifier matching the handling vehicle profile database entry", example = "45")
    private Long vehicleId;

    @Schema(description = "Unique alphanumeric display plate tracker bound to the handling physical transport frame instance", example = "MH-12-QW-5678")
    private String vehicleLicensePlate;
    
    @Schema(description = "Calculated chronological list displaying delivery locations organized by optimal spatial sequence paths mapping target drops")
    private List<DeliveryTaskResponseDTO> optimizedStops;
    
    @Schema(description = "Total cumulative driving path scale thickness connecting all scheduled nodes calculated in standard kilometers (km)", example = "12.45")
    private Double totalDistanceKm;
    
    @Schema(description = "Total algorithmically calculated operational driving time footprint needed to clear the path, scaled in minutes", example = "34.2")
    private Double totalDurationMinutes;
}