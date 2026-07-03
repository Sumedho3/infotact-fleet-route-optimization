package com.infotact.fleet.service.routing;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoutingEnginePerformanceTest {

    private NearestNeighborTspSolver tspSolver;
    private Random random;

    @BeforeEach
    void setUp() {
        this.tspSolver = new NearestNeighborTspSolver();
        this.random = new Random(42); // Fixed seed ensures reproducible geographic deviations
    }

    @Test
    @DisplayName("🚀 Benchmark: Evaluate TSP engine runtime scaling up to 50 stops")
    void evaluateAlgorithmRuntimeComplexity_ForHighVolumeStops() {

        // 1. Arrange: Build out an array matrix matching a 50-stop warehouse routing run
        int totalStops = 50;
        List<DeliveryTaskResponseDTO> mockWaypoints = new ArrayList<>();

        for (int i = 0; i < totalStops; i++) {
            DeliveryTaskResponseDTO task = new DeliveryTaskResponseDTO();
            task.setId((long) i);

            // Simulate realistic spatial distribution coordinates around a base depot hub
            task.setLatitude(18.5204 + (random.nextDouble() - 0.5) * 0.2);
            task.setLongitude(73.8567 + (random.nextDouble() - 0.5) * 0.2);

            mockWaypoints.add(task);
        }

        // 2. Generate the matching 50x50 cost measurement grid arrays
        double[][] distances = new double[totalStops][totalStops];
        double[][] durations = new double[totalStops][totalStops];

        for (int i = 0; i < totalStops; i++) {
            for (int j = 0; j < totalStops; j++) {
                if (i != j) {
                    double latDiff = mockWaypoints.get(i).getLatitude()
                            - mockWaypoints.get(j).getLatitude();

                    double lonDiff = mockWaypoints.get(i).getLongitude()
                            - mockWaypoints.get(j).getLongitude();

                    distances[i][j] =
                            Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111.0;

                    durations[i][j] = distances[i][j] * 1.5;
                }
            }
        }

        DistanceMatrix matrix = new DistanceMatrix(distances, durations);

        // 3. Act: Trigger performance profile timer logs across calculation loops
        long startTime = System.nanoTime();

        List<Integer> optimizedSequence =
                tspSolver.computeOptimizedSequence(matrix);

        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;

        System.out.println(
                "⏱️ BENCHMARK SUMMARY: Processed "
                        + totalStops
                        + " waypoints in "
                        + durationMs
                        + " ms"
        );

        // 4. Assert
        assertEquals(
                totalStops,
                optimizedSequence.size(),
                "Optimized stop tree must include all waypoint nodes."
        );

        assertTrue(
                durationMs < 100,
                "Algorithmic optimization breached the 100ms performance SLA boundary."
        );
    }
}