package com.infotact.fleet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infotact.fleet.dto.DeliveryTaskRequestDTO;
import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.DriverAssignmentRequestDTO;
import com.infotact.fleet.dto.DriverResponseDTO;
import com.infotact.fleet.dto.RouteManifestResponseDTO;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.service.FleetService;
import com.infotact.fleet.service.ManifestWorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LogisticsController {

    private final FleetService fleetService;
    private final ManifestWorkflowService manifestWorkflowService;

    public LogisticsController(FleetService fleetService,
                               ManifestWorkflowService manifestWorkflowService) {
        this.fleetService = fleetService;
        this.manifestWorkflowService = manifestWorkflowService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<DeliveryTaskResponseDTO> createDeliveryTask(
            @Valid @RequestBody DeliveryTaskRequestDTO request) {

        DeliveryTaskResponseDTO response = fleetService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/vehicles/{vehicleId}/assign/{driverId}")
    public ResponseEntity<DriverResponseDTO> registerAssetAssignment(
            @PathVariable Long vehicleId,
            @PathVariable Long driverId) {

        DriverResponseDTO response =
                fleetService.assignDriverToVehicle(driverId, vehicleId);

        return ResponseEntity.ok(response);
    }

    
    /**
     * 🚀 MAIN WORKFLOW DISPATCH RELEASE:
     * POST /api/manifests/{id}/dispatch
     * Officially kicks off physical warehouse dispatch transport activities safely.
     */
    @PostMapping("/manifests/{id}/dispatch")
    public ResponseEntity<com.infotact.fleet.dto.RouteManifestResponseDTO> finalizeDispatch(@PathVariable("id") Long id) {

        // 1. Hand off execution straight down to the workflow engine layer[cite: 14]
        com.infotact.fleet.entity.RouteManifest dispatchedManifest = manifestWorkflowService.finalizeDispatch(id);

        // 2. Map the parent fields to break the infinite entity serialization loop
        com.infotact.fleet.dto.RouteManifestResponseDTO response = new com.infotact.fleet.dto.RouteManifestResponseDTO();
        response.setId(dispatchedManifest.getId());
        response.setDriverId(dispatchedManifest.getDriverId());
        response.setStatus(dispatchedManifest.getStatus().name());
        response.setDispatchedAt(dispatchedManifest.getDispatchedAt());

        // 3. Map the optimized stop collection using your clean DTO blueprints
        if (dispatchedManifest.getOptimizedStops() != null) {
            java.util.List<com.infotact.fleet.dto.DeliveryTaskResponseDTO> stops = dispatchedManifest.getOptimizedStops().stream()
                .map(task -> new com.infotact.fleet.dto.DeliveryTaskResponseDTO(
                    task.getId(),
                    task.getDestinationAddress(),
                    task.getLatitude(),
                    task.getLongitude(),
                    task.getPackageWeightKg(),
                    task.getStatus(),
                    dispatchedManifest.getId(),
                    task.getCreatedAt(),
                    task.getUpdatedAt()
                )).toList();
            response.setOptimizedStops(stops);
        }

        // 4. Return the safe structural response mapping out to your presentation grid
        return ResponseEntity.ok(response);
    }
    
    
    
    /**
     * 🆔 OPERATIONAL MANIFEST DRIVER LINK:
     * POST /api/manifests/assign-driver
     * Binds an eligible driver asset directly to a prepared route manifest sequence safely.
     */
    @PostMapping("/manifests/assign-driver")
    public ResponseEntity<RouteManifestResponseDTO> allocateDriverToManifest(
            @Valid @RequestBody DriverAssignmentRequestDTO request) {
        
        // 1. Hand off payload matrix to the service layer orchestration workflow[cite: 16]
        RouteManifest updatedManifest = 
                manifestWorkflowService.assignDriverToManifest(request);
        
        // 2. Map the protected database entity properties cleanly into the secure outbound DTO
        RouteManifestResponseDTO response = new RouteManifestResponseDTO();
        response.setId(updatedManifest.getId());
        response.setDriverId(updatedManifest.getDriverId());
        response.setStatus(updatedManifest.getStatus().name());
        response.setDispatchedAt(updatedManifest.getDispatchedAt());
        
        // 3. Convert the inner entities to fully documented task responses[cite: 16]
        if (updatedManifest.getOptimizedStops() != null) {
            List<DeliveryTaskResponseDTO> stops = updatedManifest.getOptimizedStops().stream()
                .map(task -> new DeliveryTaskResponseDTO(
                    task.getId(),
                    task.getDestinationAddress(),
                    task.getLatitude(),
                    task.getLongitude(),
                    task.getPackageWeightKg(),
                    task.getStatus(),
                    updatedManifest.getId(),
                    task.getCreatedAt(),
                    task.getUpdatedAt()
                )).toList();
            response.setOptimizedStops(stops);
        }
        
        // 4. Return the safe structural DTO context to the API channel boundary
        return ResponseEntity.ok(response);
    }
}