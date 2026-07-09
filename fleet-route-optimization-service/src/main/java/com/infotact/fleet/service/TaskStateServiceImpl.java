package com.infotact.fleet.service;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.repository.RouteManifestRepository;

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
    
    @Autowired
    private RouteManifestRepository routeManifestRepository;

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
    
    
    @Override
    @Transactional // 🎯 CRITICAL: Ensures the parent update and child updates succeed or fail together as an atomic block
    public RouteManifest updateManifestAndCascadeStatus(Long manifestId, ManifestStatus targetStatus) {

        RouteManifest manifest = routeManifestRepository.findById(manifestId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Route manifest record not found with ID: " + manifestId));


        // 2. Validate state transition boundaries using Day 4 explicit guard matrix rules
        manifest.transitionToStatus(targetStatus);

        // 3. Mutate parent status
        manifest.setStatus(targetStatus);
        RouteManifest savedManifest = routeManifestRepository.save(manifest);

        // 4. CASCADE PIPELINE: If parent turns DISPATCHED, trigger child cascade changes down to database rows
        if (targetStatus == ManifestStatus.DISPATCHED) {
            deliveryTaskRepository.cascadeStatusForManifestTasks(manifestId, TaskStatus.DISPATCHED);
        }

        return savedManifest;
    }
}