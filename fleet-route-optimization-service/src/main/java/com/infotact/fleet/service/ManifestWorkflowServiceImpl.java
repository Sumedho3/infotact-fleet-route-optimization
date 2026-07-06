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
import com.infotact.fleet.repository.RouteManifestRepository;

@Service
public class ManifestWorkflowServiceImpl implements ManifestWorkflowService {

    @Autowired
    private RouteManifestRepository routeManifestRepository;

    @Override
    @Transactional
    public RouteManifest assignDriverToManifest(DriverAssignmentRequestDTO request) {

        // 1. Fetch the manifest target row
        RouteManifest manifest = routeManifestRepository.findById(request.getManifestId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Route manifest target record not found with ID: "
                                        + request.getManifestId()));

        // 2. CONFLICT GUARD CHECK
        List<ManifestStatus> conflictingStatuses = Arrays.asList(
                ManifestStatus.DISPATCHED,
                ManifestStatus.IN_TRANSIT
        );

        boolean isDriverBusy =
                routeManifestRepository.existsByDriverIdAndStatusIn(
                        request.getDriverId(),
                        conflictingStatuses
                );

        if (isDriverBusy) {
            throw new DeliveryStateConflictException(
                    "Cross-assignment conflict asset error: Driver with ID "
                            + request.getDriverId()
                            + " is already actively assigned to an ongoing dispatched or in-transit route manifest."
            );
        }

        // 3. Assign Driver
        manifest.setDriverId(request.getDriverId());

        return routeManifestRepository.save(manifest);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteManifest getManifestById(Long manifestId) {

        return routeManifestRepository.findById(manifestId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Route manifest record not found with ID: "
                                        + manifestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteManifest> getManifestsByStatus(String status) {

        return routeManifestRepository.findAll()
                .stream()
                .filter(manifest ->
                        manifest.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }
}