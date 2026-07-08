package com.infotact.fleet.service;

import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.model.TaskStatus;

public interface TaskStateService {

    /**
     * Safely locks and updates a single delivery task state sequence.
     */
    DeliveryTask updateTaskStatus(Long taskId, TaskStatus targetStatus);
}