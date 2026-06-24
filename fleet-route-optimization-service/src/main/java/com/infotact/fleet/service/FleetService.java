package com.infotact.fleet.service;

import java.util.List;

import com.infotact.fleet.dto.DriverRequestDTO;
import com.infotact.fleet.dto.DriverResponseDTO;
import com.infotact.fleet.dto.VehicleRequestDTO;
import com.infotact.fleet.dto.VehicleResponseDTO;

public interface FleetService {

    VehicleResponseDTO onboardVehicle(VehicleRequestDTO request);

    DriverResponseDTO onboardDriver(DriverRequestDTO request);

    VehicleResponseDTO updateMaintenanceStatus(Long vehicleId, Boolean status);

    DriverResponseDTO assignDriverToVehicle(Long driverId, Long vehicleId);

    List<VehicleResponseDTO> getAvailableVehicles();
}