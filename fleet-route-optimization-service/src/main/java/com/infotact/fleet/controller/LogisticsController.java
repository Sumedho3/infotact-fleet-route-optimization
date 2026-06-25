package com.infotact.fleet.controller;

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
import com.infotact.fleet.dto.DriverResponseDTO;
import com.infotact.fleet.service.FleetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LogisticsController {

    private final FleetService fleetService;

    public LogisticsController(FleetService fleetService) {
        this.fleetService = fleetService;
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
}