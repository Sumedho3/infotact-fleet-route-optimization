package com.infotact.fleet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
        name = "RouteOptimizationRequest",
        description = "Input payload for computing optimized vehicle distribution sequences"
)
public class RouteOptimizationRequestDTO {

    @Schema(
        description = "Unique primary sequence identification tracking key assigned to the distribution vehicle profile instance", 
        example = "45", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Vehicle selection is required for optimization deployment")
    private Long vehicleId;

    @Schema(
        description = "Ordered matrix compilation containing the specific collection of primary IDs matching pending staging tasks to assign and schedule", 
        example = "[10025, 10026, 10027]", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Optimization requests must contain at least one pending delivery task ID")
    private List<Long> taskIds;
}