package com.infotact.fleet.controller;

import com.infotact.fleet.dto.DriverRequestDTO;
import com.infotact.fleet.dto.DriverResponseDTO;
import com.infotact.fleet.dto.VehicleRequestDTO;
import com.infotact.fleet.dto.VehicleResponseDTO;
import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.service.FleetService;
import com.infotact.fleet.service.TaskStateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FleetController {

    private final FleetService fleetService;
    private final TaskStateService taskStateService;

    public FleetController(FleetService fleetService,
                           TaskStateService taskStateService) {
        this.fleetService = fleetService;
        this.taskStateService = taskStateService;
    }

    /**
     * 📱 DRIVER MOBILE PATCH ENDPOINT:
     * PATCH /api/tasks/{id}/status?targetStatus=DISPATCHED
     */
    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<DeliveryTask> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus targetStatus) {

        DeliveryTask updatedTask =
                taskStateService.updateTaskStatus(id, targetStatus);

        return ResponseEntity.ok(updatedTask);
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponseDTO> registerVehicle(
            @Valid @RequestBody VehicleRequestDTO request) {

        VehicleResponseDTO response = fleetService.onboardVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/drivers")
    public ResponseEntity<DriverResponseDTO> registerDriver(
            @Valid @RequestBody DriverRequestDTO request) {

        DriverResponseDTO response = fleetService.onboardDriver(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/vehicles/available")
    public ResponseEntity<List<VehicleResponseDTO>> fetchAvailableVehicles() {

        List<VehicleResponseDTO> availableVehicles =
                fleetService.getAvailableVehicles();

        return ResponseEntity.ok(availableVehicles);
    }

    @PostMapping("/assignments/pair")
    public ResponseEntity<DriverResponseDTO> linkDriverToVehicle(
            @RequestParam Long driverId,
            @RequestParam Long vehicleId) {

        DriverResponseDTO response =
                fleetService.assignDriverToVehicle(driverId, vehicleId);

        return ResponseEntity.ok(response);
    }
}