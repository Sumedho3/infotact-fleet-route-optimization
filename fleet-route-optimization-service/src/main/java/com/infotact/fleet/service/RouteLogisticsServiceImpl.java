package com.infotact.fleet.service;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.RouteOptimizationRequestDTO;
import com.infotact.fleet.dto.RouteOptimizationResponseDTO;
import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.entity.Vehicle;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import com.infotact.fleet.repository.RouteManifestRepository;
import com.infotact.fleet.repository.VehicleRepository;
import com.infotact.fleet.service.routing.DistanceMatrix;
import com.infotact.fleet.service.routing.RoutingMatrixEngine;
import com.infotact.fleet.service.routing.RoutingOptimizationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteLogisticsServiceImpl implements RouteLogisticsService {

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

        // 1. Fetch and validate the vehicle asset row
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Target vehicle asset not found with ID: " + request.getVehicleId()));

        // 2. Load the raw unstaged delivery tasks from MySQL matching user selections
        List<DeliveryTask> rawTasks = deliveryTaskRepository.findAllById(request.getTaskIds());
        // 🎯 FIXED FAIL-FAST GUARD: Stop execution BEFORE calling the external OSRM API
        if (rawTasks == null || rawTasks.size() < 2) {
            throw new IllegalArgumentException(
                    "Route optimization requires a minimum of 2 delivery waypoints to compute a road network matrix. Stops supplied: "
                            + (rawTasks == null ? 0 : rawTasks.size())
            );
        }
        if (rawTasks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot optimize an empty list of warehouse delivery tasks.");
        }

        // 3. Transform entity properties into coordinate data models
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

        // 4. Fetch real road network cost tables
        double[][] distances = matrixEngine.calculateDistanceMatrix(taskDtos);
        double[][] durations = matrixEngine.calculateTravelTimeMatrix(taskDtos);

        // 5. Pack into DistanceMatrix
        DistanceMatrix liveMatrix = new DistanceMatrix(distances, durations);

        // 6. Invoke the optimization engine
        RouteOptimizationResponseDTO optimizedResults =
                routingOptimizationService.computeLiveOptimizedRoute(
                        taskDtos,
                        liveMatrix,
                        vehicle.getId(),
                        vehicle.getLicensePlate());

        // 7. Persist RouteManifest
        RouteManifest manifest = new RouteManifest();
        manifest.setVehicle(vehicle);
        manifest.setTotalDistanceKm(optimizedResults.getTotalDistanceKm());
        manifest.setTotalDurationMinutes(optimizedResults.getTotalDurationMinutes());
        manifest.setStatus("OPTIMIZED");

        List<DeliveryTask> sortedEntities = new ArrayList<>();
        for (DeliveryTaskResponseDTO sortedTaskDto : optimizedResults.getOptimizedStops()) {
            DeliveryTask task = deliveryTaskRepository.findById(sortedTaskDto.getId()).orElseThrow(()-> new IllegalArgumentException("Invalid TaskId"));
            task.setStatus(TaskStatus.ASSIGNED);
            sortedEntities.add(task);
        }

        manifest.setOptimizedStops(sortedEntities);

        routeManifestRepository.save(manifest);

        return optimizedResults;
    }
}