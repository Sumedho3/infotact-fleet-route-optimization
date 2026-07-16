package com.infotact.fleet.service;

import com.infotact.fleet.dto.RouteOptimizationRequestDTO;
import com.infotact.fleet.dto.RouteOptimizationResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RouteLogisticsMemoryStabilityTest {

    @Autowired
    private RouteLogisticsService routeLogisticsService;

    @Test
    @DisplayName("⏳ Memory Stability: Validate service transaction layers against sustained accumulation faults")
    void validateLongTermTransactionMemoryFootprint() {

        // Formulate a baseline mock request payload array context
        RouteOptimizationRequestDTO request = new RouteOptimizationRequestDTO();
        request.setVehicleId(1L);
        request.setTaskIds(List.of(1L, 2L));

        Runtime runtime = Runtime.getRuntime();

        // Capture initial memory baseline
        runtime.gc();
        long memoryBeforeLoops = runtime.totalMemory() - runtime.freeMemory();

        // Execute repeated service calls
        final int executionLoops = 500;

        for (int i = 0; i < executionLoops; i++) {
            try {
                RouteOptimizationResponseDTO response =
                        routeLogisticsService.optimizeAndAssignRoute(request);

                assertNotNull(
                        response,
                        "Service returned null response during continuous iteration loops."
                );

            } catch (IllegalArgumentException e) {
                // Ignore expected business validation exceptions
            }
        }

        // Capture final memory baseline
        runtime.gc();
        long memoryAfterLoops = runtime.totalMemory() - runtime.freeMemory();

        // Calculate accumulated memory
        long memoryAccumulatedDeltaBytes =
                memoryAfterLoops - memoryBeforeLoops;

        final long maxAllowedLeakThresholdBytes =
                10 * 1024 * 1024; // 10 MB

        assertTrue(
                memoryAccumulatedDeltaBytes < maxAllowedLeakThresholdBytes,
                "🛑 Memory leak warning! Permanent heap accumulation detected: "
                        + (memoryAccumulatedDeltaBytes / 1024)
                        + " KB"
        );
    }
}