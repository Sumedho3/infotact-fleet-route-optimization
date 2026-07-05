package com.infotact.fleet.repository;

import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {

    /**
     * 🔍 STATE FILTER MATRIX:
     * Safely fetches a batch of delivery tasks matching an explicit lifecycle state status.
     * Useful for identifying unassigned warehouse tasks or auditing in-flight shipments.
     *
     * @param status The target operational runtime state enum (e.g., TaskStatus.UNASSIGNED)
     * @return A list of tasks assigned to that specific status phase.
     */
    List<DeliveryTask> findByStatus(TaskStatus status);

    /**
     * Finds tasks matching multiple states simultaneously (e.g., active delivery cycles like DISPATCHED + IN_TRANSIT).
     *
     * @param statuses Collection of operational state flags to include.
     * @return List of matching delivery tasks.
     */
    List<DeliveryTask> findByStatusIn(List<TaskStatus> statuses);
}