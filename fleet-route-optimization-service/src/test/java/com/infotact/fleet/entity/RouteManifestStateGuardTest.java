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
    @DisplayName("🛑 State Guard: Should block transition from UNASSIGNED to EN_ROUTE")
    void shouldBlockTransitionFromUnassignedToEnRoute() {
        assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.EN_ROUTE);
        });
    }

    @Test
    @DisplayName("✅ State Guard: Should allow valid transition path")
    void shouldAllowValidStateTransitionPaths() {
        assertDoesNotThrow(() -> {
            manifest.transitionToStatus(ManifestStatus.OPTIMIZED);
        });

        assertEquals(ManifestStatus.OPTIMIZED, manifest.getStatus());
    }
}