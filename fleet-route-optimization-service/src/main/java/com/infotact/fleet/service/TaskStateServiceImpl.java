package com.infotact.fleet.service;

import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class TaskStateServiceImpl implements TaskStateService {

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;

    @Override
    @Transactional
    public DeliveryTask updateTaskStatus(Long taskId, TaskStatus targetStatus) {

        // 1. Fetch record using pessimistic row lock
        DeliveryTask task = deliveryTaskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Delivery task record not found with ID: " + taskId));

        // 2. Validate transition
        task.validateTransitionTo(targetStatus);

        // 3. Update state
        task.setStatus(targetStatus);

        // 4. Save
        return deliveryTaskRepository.save(task);
    }
}