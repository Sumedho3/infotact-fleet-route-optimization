package com.infotact.fleet.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.infotact.fleet.exception.DeliveryStateConflictException;
import com.infotact.fleet.model.ManifestStatus;

class RouteManifestStateGuardTest {

    private RouteManifest manifest;

    @BeforeEach
    void setUp() {
        manifest = new RouteManifest();
        manifest.setStatus(ManifestStatus.UNASSIGNED);
    }

    @Test
    @DisplayName("🛑 State Guard: Should block direct transition from UNASSIGNED straight to DELIVERED")
    void shouldBlockTransitionFromUnassignedToDelivered() {
        assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.DELIVERED);
        });
    }

    @Test
    @DisplayName("🛑 State Guard: Should block transition from UNASSIGNED to IN_TRANSIT")
    void shouldBlockTransitionFromUnassignedToInTransit() {
        assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.IN_TRANSIT);
        });
    }

    @Test
    @DisplayName("✅ State Guard: Should allow valid transition path")
    void shouldAllowValidTransitionPaths() {
        assertDoesNotThrow(() -> {
            manifest.transitionToStatus(ManifestStatus.DISPATCHED);
        });

        assertEquals(ManifestStatus.DISPATCHED, manifest.getStatus());
    }
}