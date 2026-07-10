package com.infotact.fleet.entity;

import com.infotact.fleet.exception.DeliveryStateConflictException;
import com.infotact.fleet.model.ManifestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteManifestTest {

    private RouteManifest manifest;

    @BeforeEach
    void setUp() {
        // Initialize a clean transient entity state before each test run
        manifest = new RouteManifest();
        // Assuming your BaseEntity or initial assignment sets the default starting state
        manifest.setStatus(ManifestStatus.UNASSIGNED);
    }

    @Test
    @DisplayName("✅ Legal Sequence: UNASSIGNED to OPTIMIZED should pass smoothly")
    void testLegalTransitionToOptimized() {
        assertDoesNotThrow(() -> {
            manifest.transitionToStatus(ManifestStatus.OPTIMIZED);
        });
        assertEquals(ManifestStatus.OPTIMIZED, manifest.getStatus(),
                "Manifest status should successfully update to OPTIMIZED.");
    }

    @Test
    @DisplayName("✅ Legal Sequence: OPTIMIZED to DISPATCHED should pass smoothly")
    void testLegalTransitionToDispatched() {
        // Advance state to the required prerequisite stage
        manifest.setStatus(ManifestStatus.OPTIMIZED);

        assertDoesNotThrow(() -> {
            manifest.transitionToStatus(ManifestStatus.DISPATCHED);
        });
        assertEquals(ManifestStatus.DISPATCHED, manifest.getStatus(),
                "Manifest status should successfully update to DISPATCHED.");
    }

    @Test
    @DisplayName("🛑 Illegal Sequence: UNASSIGNED to DISPATCHED must throw exception")
    void testIllegalTransitionDirectlyToDispatched() {
        // Act & Assert: Attempting to bypass the OPTIMIZED stage must trip the business invariant guard rule
        DeliveryStateConflictException exception = assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.DISPATCHED);
        }, "Should reject direct dispatching if the route sequence hasn't been optimized yet.");

        assertTrue(exception.getMessage().contains("Transition"), "Exception message should explain the rule violation.");
        assertEquals(ManifestStatus.UNASSIGNED, manifest.getStatus(),
                "Entity state must remain completely unaltered following a failed guard check.");
    }

    @Test
    @DisplayName("🛑 Illegal Sequence: UNASSIGNED to DELIVERED must throw exception")
    void testIllegalTransitionDirectlyToDelivered() {

        assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.DELIVERED);
        }, "Should reject moving to DELIVERED straight from an UNASSIGNED state.");
    }
}