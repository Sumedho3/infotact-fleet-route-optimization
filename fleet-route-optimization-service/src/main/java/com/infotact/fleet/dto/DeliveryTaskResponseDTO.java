package com.infotact.fleet.dto;

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
