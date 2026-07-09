package com.infotact.fleet.service;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.model.TaskStatus;

public interface TaskStateService {
    /**
     * Updates parent manifest status and cascades corresponding state updates to child stops.
     */
    RouteManifest updateManifestAndCascadeStatus(Long manifestId, ManifestStatus targetStatus);
    /**
     * Safely locks and updates a single delivery task state sequence.
     */
    DeliveryTask updateTaskStatus(Long taskId, TaskStatus targetStatus);
}