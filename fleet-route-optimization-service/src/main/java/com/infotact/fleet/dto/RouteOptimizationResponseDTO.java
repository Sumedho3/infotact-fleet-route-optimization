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

    // Final optimized sequence of delivery stops
    private List<DeliveryTaskResponseDTO> optimizedStops;

    private Double totalDistanceKm;

    private Double totalDurationMinutes;
}