package com.infotact.fleet.model;

import com.infotact.fleet.exception.DeliveryStateConflictException;
import java.util.List;

public enum ManifestStatus {

    UNASSIGNED,
    DISPATCHED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED;

    /**
     * 🛡️ TRANSITION GUARD MATRIX:
     * Defines the strict legal path boundaries for a manifest lifecycle.
     */
    public void validateTransitionTo(ManifestStatus targetStatus) {

        List<ManifestStatus> allowedNextStates = switch (this) {
            case UNASSIGNED -> List.of(DISPATCHED, CANCELLED);
            case DISPATCHED -> List.of(IN_TRANSIT, CANCELLED);
            case IN_TRANSIT -> List.of(DELIVERED, CANCELLED);
            case DELIVERED, CANCELLED -> List.of();
        };

        if (!allowedNextStates.contains(targetStatus)) {
            throw new DeliveryStateConflictException(
                    "Illegal manifest state transition: Cannot change status from "
                            + this + " to " + targetStatus
            );
        }
    }
}