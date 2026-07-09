package com.infotact.fleet.repository;

import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.model.TaskStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {


    /**
     * 🔍 STATE FILTER MATRIX:
     * Safely fetches a batch of delivery tasks matching an explicit lifecycle state status.
     *
     * @param status The target operational runtime state enum.
     * @return A list of matching delivery tasks.
     */
    List<DeliveryTask> findByStatus(TaskStatus status);

    /**
     * Finds tasks matching multiple states simultaneously.
     *
     * @param statuses Collection of task states.
     * @return List of matching delivery tasks.
     */
    List<DeliveryTask> findByStatusIn(List<TaskStatus> statuses);

    /**
     * 🔒 PESSIMISTIC LOCK TRANSACTION BLANKET:
     * Issues a SELECT ... FOR UPDATE query to lock the row.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM DeliveryTask t WHERE t.id = :id")
    Optional<DeliveryTask> findByIdForUpdate(@Param("id") Long id);
}