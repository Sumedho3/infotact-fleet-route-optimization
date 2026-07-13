package com.infotact.fleet.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infotact.fleet.dto.RouteOptimizationRequestDTO;
import com.infotact.fleet.dto.RouteOptimizationResponseDTO;
import com.infotact.fleet.service.RouteLogisticsService;

import jakarta.validation.Valid;

/**
 * 🚛 FLEET DISPATCH GATEWAY:
 * Exposes endpoint resources to trigger high-throughput network matrix calculations
 * and graph-theory vehicle stop sequencing optimization routines.
 */
@RestController
@RequestMapping("/api/routes")
@Tag(
        name = "Fleet Dispatch Gateway",
        description = "Core algorithmic endpoints for calculating high-throughput vehicle routing and stop optimization matrices."
)
public class RouteOptimizationController {

    @Autowired
    private RouteLogisticsService routeLogisticsService;

    /**
     * Consumes warehouse dispatch batches and generates an optimized, road-network sorted route manifest.
     *
     * @param request Payload containing the target vehicle ID and list of unstaged delivery task IDs.
     * @return A response payload detailing the optimized stop sequence and cumulative route costs.
     */
    @Operation(
            summary = "🚀 Generate Optimized Route Manifest",
            description = "Consumes raw warehouse dispatch batch parameters to calculate and persist a sequenced, graph-theory optimized waypoint path manifest using the core routing engines."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Route manifest successfully optimized and persisted.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RouteOptimizationResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation constraints failure or insufficient waypoints provided in request payload.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing anomaly or matrix algorithm execution fault.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping("/optimize")
    public ResponseEntity<RouteOptimizationResponseDTO> generateOptimizedRoute(
            @Valid @RequestBody RouteOptimizationRequestDTO request) {

        RouteOptimizationResponseDTO optimizedResponse =
                routeLogisticsService.optimizeAndAssignRoute(request);

        return new ResponseEntity<>(optimizedResponse, HttpStatus.CREATED);
    }
}