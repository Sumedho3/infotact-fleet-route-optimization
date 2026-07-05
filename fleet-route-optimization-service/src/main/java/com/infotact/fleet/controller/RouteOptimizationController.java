package com.infotact.fleet.controller;

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
public class RouteOptimizationController {

    @Autowired
    private RouteLogisticsService routeLogisticsService;

    /**
     * Consumes warehouse dispatch batches and generates an optimized, road-network sorted route manifest.
     *
     * @param request Payload containing the target vehicle ID and list of unstaged delivery task IDs.
     * @return A response payload detailing the optimized stop sequence and cumulative route costs.
     */
    @PostMapping("/optimize")
    public ResponseEntity<RouteOptimizationResponseDTO> generateOptimizedRoute(
            @Valid @RequestBody RouteOptimizationRequestDTO request) {

        RouteOptimizationResponseDTO optimizedResponse =
                routeLogisticsService.optimizeAndAssignRoute(request);

        return new ResponseEntity<>(optimizedResponse, HttpStatus.CREATED);
    }
}