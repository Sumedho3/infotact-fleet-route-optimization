package com.infotact.fleet.service.routing;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import java.util.List;

/**
 * 🎯 FOUNDATIONAL INTERFACE CONTRACT:
 * Defines the core abstraction layer for resolving distance and travel time metadata
 * across sequential dispatch waypoint pairs.
 */
public interface RoutingMatrixEngine {

    /**
     * Accepts a list of sequenced staging tasks, extracts their latitude/longitude pairs,
     * and constructs an internal cost representation of physical distances.
     *
     * @param waypoints List of delivery tasks containing coordinate pairs
     * @return A double array matrix representing structural distance costs between nodes
     */
    double[][] calculateDistanceMatrix(List<DeliveryTaskResponseDTO> waypoints);

    /**
     * Accepts a list of sequenced staging tasks and constructs an internal cost matrix
     * representing road travel durations in seconds or minutes.
     *
     * @param waypoints List of delivery tasks containing coordinate pairs
     * @return A double array matrix representing time durations between nodes
     */
    double[][] calculateTravelTimeMatrix(List<DeliveryTaskResponseDTO> waypoints);

}