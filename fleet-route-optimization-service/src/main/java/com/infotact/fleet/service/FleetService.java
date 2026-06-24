package com.infotact.fleet.service;

import com.infotact.fleet.dto.*;
import java.util.List;

public interface FleetService {
    VehicleResponseDTO onboardVehicle(VehicleRequestDTO request);
    DriverResponseDTO onboardDriver(DriverRequestDTO request);
    VehicleResponseDTO updateMaintenanceStatus(Long vehicleId, Boolean status);

    List<VehicleResponseDTO> getAvailableVehicles();
}