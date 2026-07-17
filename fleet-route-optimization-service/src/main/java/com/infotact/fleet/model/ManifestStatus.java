package com.infotact.fleet.model;

import com.infotact.fleet.exception.DeliveryStateConflictException;
import java.util.List;

public enum ManifestStatus {

    UNASSIGNED,
    DISPATCHED,
    IN_TRANSIT,
    DELIVERED,
    OPTIMIZED,
    CANCELLED;

    /**
     * 🏁 TRANSITION GUARD MATRIX:
     * Aligns with RouteManifest.java tracking validation logic.
     */
    public void validateTransitionTo(ManifestStatus targetStatus) {

        List<ManifestStatus> allowedNextStates = switch (this) {
            case UNASSIGNED -> List.of(OPTIMIZED, CANCELLED);
            case OPTIMIZED -> List.of(DISPATCHED, UNASSIGNED, CANCELLED);
            case DISPATCHED -> List.of(IN_TRANSIT, CANCELLED);
            case IN_TRANSIT -> List.of(DELIVERED, CANCELLED);
            case DELIVERED -> List.of();
            case CANCELLED -> List.of();
        };

        if (!allowedNextStates.contains(targetStatus)) {
            throw new DeliveryStateConflictException(
                    "Illegal manifest state transition: Cannot change status from "
                            + this + " to " + targetStatus
            );
        }
    }
}