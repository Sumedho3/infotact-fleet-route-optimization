package com.infotact.fleet.model;

import com.infotact.fleet.exception.DeliveryStateConflictException;
import java.util.List;

public enum TaskStatus {

    UNASSIGNED,
    ASSIGNED,
    DISPATCHED,
    IN_TRANSIT,
    DELIVERED;

    /**
     * 🛡️ TASK TRANSITION GUARD MATRIX:
     * Restricts drivers and backend pipelines from skipping individual delivery task states.
     */
    public void validateTransitionTo(TaskStatus targetStatus) {

        List<TaskStatus> allowedNextStates = switch (this) {
            case UNASSIGNED -> List.of(ASSIGNED);
            case ASSIGNED -> List.of(DISPATCHED, UNASSIGNED);
            case DISPATCHED -> List.of(IN_TRANSIT);
            case IN_TRANSIT -> List.of(DELIVERED);
            case DELIVERED -> List.of();
        };

        if (!allowedNextStates.contains(targetStatus)) {
            throw new DeliveryStateConflictException(
                    "Illegal delivery task state transition: Cannot change status from "
                            + this + " to " + targetStatus
            );
        }
    }
}