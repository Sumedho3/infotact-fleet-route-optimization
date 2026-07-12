package com.infotact.fleet.service.routing;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NearestNeighborTspSolverStressTest {

    private NearestNeighborTspSolver tspSolver;
    private Random random;

    @BeforeEach
    void setUp() {
        // Initialize the concrete solver instance under evaluation
        this.tspSolver = new NearestNeighborTspSolver();
        this.random = new Random(42); // Fixed seed ensures predictable testing cycles
    }

    @Test
    @DisplayName("📈 Matrix Stress: Solver must cleanly compute routes across varied bulk dimensions")
    void evaluateTspSolverPerformanceUnderBulkStresses() {
        // Define an array of test sizes representing standard, large, and intense waypoint datasets
        int[] datasetSizes = {5, 15, 30, 50};

        for (int size : datasetSizes) {
            // 1. Generate a mock symmetric distance cost matrix layout for the target size
            DistanceMatrix mockMatrix = generateMockDistanceMatrix(size);

            // 2. Execute and monitor the execution duration boundary limits
            long startTimestamp = System.currentTimeMillis();
            List<Integer> optimizedSequence = tspSolver.computeOptimizedSequence(mockMatrix);
            long endTimestamp = System.currentTimeMillis();

            long executionDuration = endTimestamp - startTimestamp;

            // 3. Verify core structural invariants of the resulting route path
            assertNotNull(optimizedSequence, "The computed sequence must never be null.");
            assertEquals(size, optimizedSequence.size(), "The optimized path must visit exactly every waypoint location.");

            // Ensure no duplicate stops exist in the path (each node must be visited exactly once)
            long uniqueStopsCount = optimizedSequence.stream().distinct().count();
            assertEquals(size, uniqueStopsCount, "The route path sequence must contain unique node selections.");

            // 4. Enforce strict processing performance limits (e.g., 50 nodes should compute in under 500ms)
            assertTrue(executionDuration < 500,
                    "Routing matrix calculations exceeded performance threshold for size: " + size);

            // Verify total calculation rules do not crash on the computed sequence list
            double calculatedDistance =
                    tspSolver.calculateTotalDistance(optimizedSequence, mockMatrix);

            assertTrue(calculatedDistance >= 0.0,
                    "Total cumulative distance must yield a valid absolute calculation metric.");
        }
    }

    /**
     * Helper method to generate dummy distance matrix values matching expected production parameter frameworks.
     */
    private DistanceMatrix generateMockDistanceMatrix(int nodesCount) {
        double[][] dataGrid = new double[nodesCount][nodesCount];

        for (int i = 0; i < nodesCount; i++) {
            for (int j = 0; j < nodesCount; j++) {
                if (i == j) {
                    dataGrid[i][j] = 0.0; // Distance to itself is always zero
                } else {
                    // Generate realistic transit distance ranges between 1.0 km and 45.0 km
                    double distanceValue = 1.0 + (44.0 * random.nextDouble());
                    dataGrid[i][j] = distanceValue;
                    dataGrid[j][i] = distanceValue; // Enforce symmetric distance equality properties
                }
            }
        }

        // Wrap the raw matrix double grid into your production DistanceMatrix type wrapper
        return new DistanceMatrix(dataGrid, dataGrid);
    }
}