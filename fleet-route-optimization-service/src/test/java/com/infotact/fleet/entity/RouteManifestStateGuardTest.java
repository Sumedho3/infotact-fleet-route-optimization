package com.infotact.fleet.entity;

import com.infotact.fleet.exception.DeliveryStateConflictException;
import com.infotact.fleet.model.ManifestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldBlockTransitionFromUnassignedToEnRoute() {
        assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.IN_TRANSIT);
        });
    }

    @Test
    @DisplayName("✅ State Guard: Should allow valid transition path")
    void shouldAllowValidTransitionPaths() {
        // According to production code, UNASSIGNED can cleanly move to DISPATCHED
        assertDoesNotThrow(() -> {
            manifest.transitionToStatus(ManifestStatus.DISPATCHED);
        });
        
        assertEquals(ManifestStatus.DISPATCHED, manifest.getStatus());
    }
}