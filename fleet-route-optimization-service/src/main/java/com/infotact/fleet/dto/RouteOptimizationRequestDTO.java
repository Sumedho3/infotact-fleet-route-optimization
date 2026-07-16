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

    @NotNull(message = "Vehicle selection is required for optimization deployment")
    private Long vehicleId;

    @NotEmpty(message = "Optimization requests must contain at least one pending delivery task ID")
    private List<Long> taskIds;
}