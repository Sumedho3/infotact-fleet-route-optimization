package com.infotact.fleet.service;

import com.infotact.fleet.dto.DriverAssignmentRequestDTO;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.RouteManifestRepository;
import com.infotact.fleet.exception.DeliveryStateConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ManifestWorkflowServiceImpl implements ManifestWorkflowService {

    @Autowired
    private RouteManifestRepository routeManifestRepository;

    @Autowired
    private TaskStateService taskStateService;

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
                .orElseThrow(() -> new NoSuchElementException(
                        "Route manifest record not found with ID: " + manifestId));

        // 2. Prevent driver double booking
        List<ManifestStatus> conflictingStatuses =
                Arrays.asList(ManifestStatus.DISPATCHED, ManifestStatus.IN_TRANSIT);

        boolean isDriverBusy =
                routeManifestRepository.existsByDriverIdAndStatusIn(driverId, conflictingStatuses);

        if (isDriverBusy) {
            throw new DeliveryStateConflictException(
                    "Cross-assignment conflict asset error: Driver with ID "
                            + driverId
                            + " is already actively assigned to an ongoing dispatched or in-transit route manifest.");
        }

        // 3. Ensure manifest is assignable
        if (manifest.getStatus() != ManifestStatus.UNASSIGNED) {
            throw new DeliveryStateConflictException(
                    "Compilation Failure: Cannot link driver to a manifest that is already in "
                            + manifest.getStatus()
                            + " status.");
        }

        // 4. Link driver
        manifest.setDriverId(driverId);

        // 5. Update underlying tasks
        if (manifest.getOptimizedStops() != null) {
            manifest.getOptimizedStops().forEach(task -> {
                if (task.getStatus() == TaskStatus.UNASSIGNED) {
                    task.setStatus(TaskStatus.ASSIGNED);
                }
            });
        }

        // 6. Save changes
        return routeManifestRepository.save(manifest);
    }

    @Override
    @Transactional
    public RouteManifest finalizeDispatch(Long manifestId) {

        // 1. Fetch manifest
        RouteManifest manifest = routeManifestRepository.findById(manifestId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Route manifest record not found with ID: " + manifestId));

        // 2. Ensure driver exists
        if (manifest.getDriverId() == null) {
            throw new DeliveryStateConflictException(
                    "Dispatch Release Failure: Cannot release manifest "
                            + manifestId
                            + " because no driver profile is linked to it.");
        }

        // 3. Cascade status update
        RouteManifest updatedManifest =
                taskStateService.updateManifestAndCascadeStatus(
                        manifestId,
                        ManifestStatus.DISPATCHED);

        // 4. Record departure timestamp
        updatedManifest.setDepartureAt(LocalDateTime.now());

        // 5. Save timestamp
        return routeManifestRepository.save(updatedManifest);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteManifest getManifestById(Long manifestId) {
        return routeManifestRepository.findById(manifestId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Route manifest record not found with ID: " + manifestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteManifest> getManifestsByStatus(String status) {
        return routeManifestRepository.findAll().stream()
                .filter(m -> m.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }
}