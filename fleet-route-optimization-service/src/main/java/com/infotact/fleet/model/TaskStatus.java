package com.infotact.fleet.model;

import com.infotact.fleet.exception.DeliveryStateConflictException;
import java.util.List;

public enum TaskStatus {

    UNASSIGNED,
    ASSIGNED,
    DISPATCHED,
    IN_TRANSIT,
    DELIVERED,
	CANCELLED;

    /**
     * 🛡️ TASK TRANSITION GUARD MATRIX:
     * Restricts drivers and backend pipelines from skipping individual delivery task states.
     */
	public void validateTransitionTo(TaskStatus targetStatus) {

        List<TaskStatus> allowedNextStates = switch (this) {
            case UNASSIGNED -> List.of(ASSIGNED, CANCELLED);
            case ASSIGNED -> List.of(DISPATCHED, CANCELLED);
            case DISPATCHED -> List.of(IN_TRANSIT, CANCELLED);
            case IN_TRANSIT -> List.of(DELIVERED, CANCELLED);
            case DELIVERED -> List.of();
            case CANCELLED -> List.of(); // 🎯 ADDED: This covers the missing case to fix the compiler error
        };

        if (!allowedNextStates.contains(targetStatus)) {
            throw new DeliveryStateConflictException(
                    "Illegal delivery task state transition: Cannot change status from "
                            + this + " to " + targetStatus
            );
        }
    }
}