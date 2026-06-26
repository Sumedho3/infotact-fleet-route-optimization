package com.infotact.fleet.controller;

import com.infotact.fleet.dto.DriverRequestDTO;
import com.infotact.fleet.dto.DriverResponseDTO;
import com.infotact.fleet.dto.VehicleRequestDTO;
import com.infotact.fleet.dto.VehicleResponseDTO;
import com.infotact.fleet.service.FleetService;
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

    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;
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