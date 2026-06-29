package com.infotact.fleet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationResponseDTO {

    private Long vehicleId;
    private String vehicleLicensePlate;
    
    // The final algorithmically sorted sequence of tasks to follow
    private List<DeliveryTaskResponseDTO> optimizedStops;
    
    private Double totalDistanceKm;
    private Double totalDurationMinutes;
}