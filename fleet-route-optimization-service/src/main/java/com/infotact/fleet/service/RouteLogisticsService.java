package com.infotact.fleet.service;

import com.infotact.fleet.dto.RouteOptimizationRequestDTO;
import com.infotact.fleet.dto.RouteOptimizationResponseDTO;

/**
 * CORE LOGISTICS INTERFACE:
 * Establishes the high-level business contract for warehouse dispatching operations.
 * It provides the processing hooks to orchestrate the route optimization pipeline.
 */
public interface RouteLogisticsService {

    /**
     * Processing hook to take raw, unstaged delivery tasks, validate asset availability,
     * invoke the matrix routing solvers, and persist the finalized optimized RouteManifest.
     *
     * @param request Payload containing the target vehicle asset ID
     *                and the list of delivery task IDs to cluster.
     * @return Fully calculated route execution summary including
     *         sorted stops, distance, and duration totals.
     */
    RouteOptimizationResponseDTO optimizeAndAssignRoute(RouteOptimizationRequestDTO request);
}