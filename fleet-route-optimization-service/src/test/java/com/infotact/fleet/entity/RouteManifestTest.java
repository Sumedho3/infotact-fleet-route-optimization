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
        manifest = new RouteManifest();
        manifest.setStatus(ManifestStatus.UNASSIGNED);
    }

    @Test
    @DisplayName("🛑 Illegal Sequence: UNASSIGNED to OPTIMIZED must be rejected by production rules")
    void testLegalTransitionToOptimized() {
        assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.OPTIMIZED);
        });
    }

    @Test
    @DisplayName("✅ Legal Sequence: UNASSIGNED to DISPATCHED should pass smoothly")
    void testLegalTransitionDirectlyToDispatched() {
        assertDoesNotThrow(() -> {
            manifest.transitionToStatus(ManifestStatus.DISPATCHED);
        });
        assertEquals(ManifestStatus.DISPATCHED, manifest.getStatus(), 
                "Manifest status should successfully update to DISPATCHED from UNASSIGNED.");
    }

    @Test
    @DisplayName("✅ Legal Sequence: OPTIMIZED to DISPATCHED should pass smoothly")
    void testLegalTransitionToDispatched() {
        manifest.setStatus(ManifestStatus.OPTIMIZED);

        assertDoesNotThrow(() -> {
            manifest.transitionToStatus(ManifestStatus.DISPATCHED);
        });
        assertEquals(ManifestStatus.DISPATCHED, manifest.getStatus(), 
                "Manifest status should successfully update to DISPATCHED from OPTIMIZED.");
    }

    @Test
    @DisplayName("🛑 Illegal Sequence: UNASSIGNED to DELIVERED must throw exception")
    void testIllegalTransitionDirectlyToDelivered() {
        // 🎯 FIX: Changed ManifestStatus.COMPLETED to the actual valid production enum ManifestStatus.DELIVERED
        assertThrows(DeliveryStateConflictException.class, () -> {
            manifest.transitionToStatus(ManifestStatus.DELIVERED); 
        }, "Should reject jumping straight to DELIVERED from an UNASSIGNED state.");
    }
}