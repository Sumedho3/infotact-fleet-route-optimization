package com.infotact.fleet.service.routing;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class RoutingEngineStabilityTest {

    private NearestNeighborTspSolver tspSolver;

    @BeforeEach
    void setUp() {
        this.tspSolver = new NearestNeighborTspSolver();
    }

    @Test
    @DisplayName("🛑 Edge Case: Verify engine processes duplicate delivery coordinates without infinite loops")
    void verifyEngineHandlesDuplicateCoordinates_WithoutAlgorithmicTraps() {

        // 1. Arrange: Create a task list where stop 2 and stop 3 have identical coordinates
        List<DeliveryTaskResponseDTO> duplicateWaypoints = new ArrayList<>();

        // Base Depot Stop
        DeliveryTaskResponseDTO depot = new DeliveryTaskResponseDTO();
        depot.setId(1L);
        depot.setLatitude(18.5204);
        depot.setLongitude(73.8567);
        duplicateWaypoints.add(depot);

        // First Delivery Location
        DeliveryTaskResponseDTO stopA = new DeliveryTaskResponseDTO();
        stopA.setId(2L);
        stopA.setLatitude(18.5410);
        stopA.setLongitude(73.8790);
        duplicateWaypoints.add(stopA);

        // Second Delivery Location (EXACT DUPLICATE OF STOP A COORDINATES)
        DeliveryTaskResponseDTO stopBDuplicate = new DeliveryTaskResponseDTO();
        stopBDuplicate.setId(3L);
        stopBDuplicate.setLatitude(18.5410);
        stopBDuplicate.setLongitude(73.8790);
        duplicateWaypoints.add(stopBDuplicate);

        int totalNodes = duplicateWaypoints.size();

        double[][] distances = new double[totalNodes][totalNodes];
        double[][] durations = new double[totalNodes][totalNodes];

        // 2. Generate the cost matrix
        for (int i = 0; i < totalNodes; i++) {
            for (int j = 0; j < totalNodes; j++) {
                if (i != j) {
                    double latDiff = duplicateWaypoints.get(i).getLatitude()
                            - duplicateWaypoints.get(j).getLatitude();

                    double lonDiff = duplicateWaypoints.get(i).getLongitude()
                            - duplicateWaypoints.get(j).getLongitude();

                    distances[i][j] =
                            Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111.0;

                    durations[i][j] = distances[i][j] * 1.5;
                }
            }
        }

        DistanceMatrix matrix = new DistanceMatrix(distances, durations);

        // 3. Verify algorithm doesn't enter an infinite loop
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {

            List<Integer> optimizedSequence =
                    tspSolver.computeOptimizedSequence(matrix);

            // 4. Verify all nodes are visited
            assertEquals(
                    3,
                    optimizedSequence.size(),
                    "The optimization sequence must cleanly visit all nodes, including identical spatial locations."
            );
        });
    }
}