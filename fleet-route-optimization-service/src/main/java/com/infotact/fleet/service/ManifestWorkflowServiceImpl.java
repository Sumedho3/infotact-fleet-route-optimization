package com.infotact.fleet.service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infotact.fleet.dto.DriverAssignmentRequestDTO;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.exception.DeliveryStateConflictException;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.RouteManifestRepository;

@Service
public class ManifestWorkflowServiceImpl implements ManifestWorkflowService {

    @Autowired
    private RouteManifestRepository routeManifestRepository;

    @Override
    @Transactional
    public RouteManifest assignDriverToManifest(DriverAssignmentRequestDTO request) {
        return compileAndLinkManifest(request.getManifestId(), request.getDriverId());
    }

    @Override
    @Transactional
    public RouteManifest compileAndLinkManifest(Long manifestId, Long driverId) {

        // 1. Fetch the target optimized manifest record
        RouteManifest manifest = routeManifestRepository.findById(manifestId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Route manifest record not found with ID: " + manifestId));

        // 2. CONFLICT GUARD CHECK
        List<ManifestStatus> conflictingStatuses = Arrays.asList(
                ManifestStatus.DISPATCHED,
                ManifestStatus.IN_TRANSIT);

        boolean isDriverBusy =
                routeManifestRepository.existsByDriverIdAndStatusIn(
                        driverId,
                        conflictingStatuses);

        if (isDriverBusy) {
            throw new DeliveryStateConflictException(
                    "Cross-assignment conflict asset error: Driver with ID "
                            + driverId
                            + " is already actively assigned to an ongoing dispatched or in-transit route manifest.");
        }

        // 3. STATE INVARIANT CHECK
        if (manifest.getStatus() != ManifestStatus.UNASSIGNED) {
            throw new DeliveryStateConflictException(
                    "Compilation Failure: Cannot link driver to a manifest that is already in "
                            + manifest.getStatus()
                            + " status.");
        }

        // 4. LINK ASSETS
        manifest.setDriverId(driverId);

        // 5. UPDATE TASK STATES
        if (manifest.getOptimizedStops() != null) {

            manifest.getOptimizedStops().forEach(task -> {

                if (task.getStatus() == TaskStatus.UNASSIGNED) {

                    task.setStatus(TaskStatus.ASSIGNED);

                }

            });

        }

        // 6. SAVE
        return routeManifestRepository.save(manifest);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteManifest getManifestById(Long manifestId) {

        return routeManifestRepository.findById(manifestId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Route manifest record not found with ID: " + manifestId));

    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteManifest> getManifestsByStatus(String status) {

        return routeManifestRepository.findAll()
                .stream()
                .filter(m -> m.getStatus().name().equalsIgnoreCase(status))
                .toList();

    }
}