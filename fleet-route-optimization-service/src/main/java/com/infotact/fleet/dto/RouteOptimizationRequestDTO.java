package com.infotact.fleet.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationRequestDTO {

    @NotNull(message = "Vehicle selection is required for optimization deployment")
    private Long vehicleId;

    @NotEmpty(message = "Optimization requests must contain at least one pending delivery task ID")
    private List<Long> taskIds;
}