package com.infotact.fleet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.RouteOptimizationRequestDTO;
import com.infotact.fleet.dto.RouteOptimizationResponseDTO;
import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.entity.Vehicle;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import com.infotact.fleet.repository.RouteManifestRepository;
import com.infotact.fleet.repository.VehicleRepository;
import com.infotact.fleet.service.routing.DistanceMatrix;
import com.infotact.fleet.service.routing.RoutingMatrixEngine;
import com.infotact.fleet.service.routing.RoutingOptimizationServiceImpl;

@Service
public class RouteLogisticsServiceImpl implements RouteLogisticsService {

    private static final Logger log = LoggerFactory.getLogger(RouteLogisticsServiceImpl.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;

    @Autowired
    private RouteManifestRepository routeManifestRepository;

    @Autowired
    private RoutingOptimizationServiceImpl routingOptimizationService;

    @Autowired
    private RoutingMatrixEngine matrixEngine;

    @Override
    @Transactional
    public RouteOptimizationResponseDTO optimizeAndAssignRoute(RouteOptimizationRequestDTO request) {

        log.info("🚀 [DISPATCH LIFECYCLE] Initiating route optimization transaction for Vehicle ID: [{}]",
                request.getVehicleId());

        // 1. Fetch Target Vehicle Asset
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> {
                    log.error("❌ [DISPATCH CRITICAL] Optimization aborted. Vehicle not found with ID: [{}]",
                            request.getVehicleId());
                    return new IllegalArgumentException(
                            "Target vehicle asset not found with ID: " + request.getVehicleId());
                });

        // 2. Fetch Selected Delivery Tasks
        List<DeliveryTask> rawTasks = deliveryTaskRepository.findAllById(request.getTaskIds());

        if (rawTasks == null || rawTasks.isEmpty()) {
            log.warn("⚠️ [DISPATCH VALIDATION] Aborting optimization layout. Target task payload array is completely empty.");
            throw new IllegalArgumentException("Cannot optimize an empty list of warehouse delivery tasks.");
        }

        if (rawTasks.size() < 2) {
            log.warn("⚠️ [DISPATCH VALIDATION] Aborting optimization layout. Insufficient stop markers supplied: [{}]",
                    rawTasks.size());

            throw new IllegalArgumentException(
                    "Route optimization requires a minimum of 2 delivery waypoints to compute a road network matrix. Stops supplied: "
                            + rawTasks.size());
        }

        // 🎯 3. VEHICLE CAPACITY GUARD CHECK: Verify total package payload against vehicle capacity
        double totalPackageWeightKg = rawTasks.stream()
                .mapToDouble(DeliveryTask::getPackageWeightKg)
                .sum();

        log.debug("[DISPATCH TRACE] Total payload weight: [{} kg], Vehicle capacity limit: [{} kg]",
                totalPackageWeightKg, vehicle.getCapacityKg());

        if (totalPackageWeightKg > vehicle.getCapacityKg()) {
            log.error("❌ [DISPATCH CRITICAL] Optimization aborted. Payload weight [{} kg] exceeds vehicle capacity [{} kg] for Vehicle ID: [{}]",
                    totalPackageWeightKg, vehicle.getCapacityKg(), vehicle.getId());

            throw new IllegalArgumentException(
                    String.format("Vehicle capacity exceeded! Total package weight (%.2f kg) exceeds maximum payload capacity (%.2f kg) for Vehicle ID: %d",
                            totalPackageWeightKg, vehicle.getCapacityKg(), vehicle.getId()));
        }

        // 4. Map DB Tasks to DTO Objects
        log.debug("[DISPATCH TRACE] Mapping [{}] database task entities to DTO objects.", rawTasks.size());

        List<DeliveryTaskResponseDTO> taskDtos = rawTasks.stream().map(task -> {
            DeliveryTaskResponseDTO dto = new DeliveryTaskResponseDTO();
            dto.setId(task.getId());
            dto.setDestinationAddress(task.getDestinationAddress());
            dto.setLatitude(task.getLatitude());
            dto.setLongitude(task.getLongitude());
            dto.setPackageWeightKg(task.getPackageWeightKg());
            dto.setStatus(task.getStatus());
            return dto;
        }).collect(Collectors.toList());

        // 5. Calculate Distance & Duration Matrices
        log.debug("[DISPATCH TRACE] Requesting distance matrix from routing engine.");

        double[][] distances = matrixEngine.calculateDistanceMatrix(taskDtos);
        double[][] durations = matrixEngine.calculateTravelTimeMatrix(taskDtos);

        DistanceMatrix liveMatrix = new DistanceMatrix(distances, durations);

        // 6. Compute Optimal Route Sequence
        log.debug("[DISPATCH TRACE] Executing optimization algorithm.");

        RouteOptimizationResponseDTO optimizedResults =
                routingOptimizationService.computeLiveOptimizedRoute(
                        taskDtos,
                        liveMatrix,
                        vehicle.getId(),
                        vehicle.getLicensePlate());

        // 7. Manage Manifest Record (Reuse existing OPTIMIZED manifest or build new)
        log.debug("[DISPATCH TRACE] Checking for existing optimized manifest.");

        Optional<RouteManifest> existingManifest =
                routeManifestRepository.findByVehicleIdAndStatus(
                        vehicle.getId(),
                        ManifestStatus.OPTIMIZED);

        RouteManifest manifest;

        if (existingManifest.isPresent()) {
            manifest = existingManifest.get();

            log.info("🔄 [DISPATCH LIFECYCLE] Existing manifest found for Vehicle ID: [{}]. Reusing manifest.",
                    vehicle.getId());

            if (manifest.getOptimizedStops() != null) {
                log.debug("[DISPATCH TRACE] Resetting [{}] previous delivery tasks.",
                        manifest.getOptimizedStops().size());

                for (DeliveryTask task : manifest.getOptimizedStops()) {
                    task.setStatus(TaskStatus.UNASSIGNED);
                }

                manifest.getOptimizedStops().clear();
            }

        } else {
            log.debug("[DISPATCH TRACE] Creating new RouteManifest.");

            manifest = new RouteManifest();
            manifest.setVehicle(vehicle);
        }

        manifest.setTotalDistanceKm(optimizedResults.getTotalDistanceKm());
        manifest.setTotalDurationMinutes(optimizedResults.getTotalDurationMinutes());
        manifest.setStatus(ManifestStatus.OPTIMIZED);

        // 8. Associate Sorted Tasks to Manifest
        List<DeliveryTask> sortedEntities = new ArrayList<>();

        for (DeliveryTaskResponseDTO dto : optimizedResults.getOptimizedStops()) {
            DeliveryTask task = deliveryTaskRepository.findById(dto.getId())
                    .orElseThrow(() -> {
                        log.error("❌ [DISPATCH CRITICAL] Task not found: [{}]", dto.getId());
                        return new IllegalArgumentException("Invalid TaskId: " + dto.getId());
                    });

            task.setStatus(TaskStatus.ASSIGNED);
            sortedEntities.add(task);
        }

        manifest.setOptimizedStops(sortedEntities);

        // 9. Persist Manifest Changes
        routeManifestRepository.save(manifest);

        log.info("✅ [DISPATCH LIFECYCLE] Route optimization completed successfully. Manifest ID: [{}], Distance: [{} km]",
                manifest.getId(),
                manifest.getTotalDistanceKm());

        return optimizedResults;
    }
}