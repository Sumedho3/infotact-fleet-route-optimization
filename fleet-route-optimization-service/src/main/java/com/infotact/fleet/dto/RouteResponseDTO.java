package com.infotact.fleet.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDTO {
	
	private Long id;
    private Double totalDistanceKm;
    private Long totalDurationMinutes;
    private String status;
    private List<DeliveryTaskResponseDTO> orderedTasks; // Structured sequence of destinations
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
