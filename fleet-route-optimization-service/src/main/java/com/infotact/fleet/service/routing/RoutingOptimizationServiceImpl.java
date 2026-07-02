package com.infotact.fleet.service.routing;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.RouteOptimizationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoutingOptimizationServiceImpl {

    @Autowired
    private NearestNeighborTspSolver tspSolver;

    /**
     * Integrates live network matrix metrics directly into the sequencing engine layout.
     * Maps calculated index values back into organized delivery task structures.
     *
     * @param tasks The list of target tasks containing location coordinates.
     * @param liveMatrix The live real-road distance matrix vault fetched from OSRM.
     * @param vehicleId The target vehicle processing this run.
     * @param licensePlate The vehicle's license plate string.
     * @return A fully populated RouteOptimizationResponseDTO containing optimized metrics.
     */
    public RouteOptimizationResponseDTO computeLiveOptimizedRoute(
            List<DeliveryTaskResponseDTO> tasks,
            DistanceMatrix liveMatrix,
            Long vehicleId,
            String licensePlate) {
        // 🎯 DAY 6 GUARD CHECK: Enforce threshold capacity validation bounds
        if (tasks == null || tasks.size() < 2) {
            throw new IllegalArgumentException(
                    "Route optimization sequencing requires a minimum threshold of 2 delivery waypoints (Stops supplied: "
                            + (tasks == null ? 0 : tasks.size()) + ")"
            );
        }

        // 1. Run the TSP Nearest Neighbor sorting brain using the real-world road matrix
        List<Integer> optimizedIndexes = tspSolver.computeOptimizedSequence(liveMatrix);

        // 2. Map the mathematical sorted index sequence back to the actual Task DTO data
        List<DeliveryTaskResponseDTO> sortedTasks = new ArrayList<>();
        for (Integer index : optimizedIndexes) {
            sortedTasks.add(tasks.get(index));
        }

        // 3. Compute cumulative road parameters from the live matrix grid
        double totalDistance = tspSolver.calculateTotalDistance(optimizedIndexes, liveMatrix);
        double totalDuration = tspSolver.calculateTotalDuration(optimizedIndexes, liveMatrix);

        // 4. Bundle everything cleanly into the final structural response DTO
        RouteOptimizationResponseDTO response = new RouteOptimizationResponseDTO();
        response.setVehicleId(vehicleId);
        response.setVehicleLicensePlate(licensePlate);
        response.setOptimizedStops(sortedTasks);
        response.setTotalDistanceKm(totalDistance);
        response.setTotalDurationMinutes(totalDuration);

        return response;
    }
}