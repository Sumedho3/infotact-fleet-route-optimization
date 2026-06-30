package com.infotact.fleet.service;

import com.infotact.fleet.dto.*;
import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.entity.Driver;
import com.infotact.fleet.entity.Vehicle;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import com.infotact.fleet.repository.DriverRepository;
import com.infotact.fleet.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FleetServiceImpl implements FleetService {

	private final VehicleRepository vehicleRepository;
	private final DriverRepository driverRepository;
	private final DeliveryTaskRepository deliveryTaskRepository;
	
	public FleetServiceImpl(VehicleRepository vehicleRepository,
	                        DriverRepository driverRepository,
	                        DeliveryTaskRepository deliveryTaskRepository) {
	    this.vehicleRepository = vehicleRepository;
	    this.driverRepository = driverRepository;
	    this.deliveryTaskRepository = deliveryTaskRepository;
	}

	@Override
	public VehicleResponseDTO onboardVehicle(VehicleRequestDTO request) {
	    Vehicle vehicle = new Vehicle();
	    vehicle.setLicensePlate(request.getLicensePlate());
	    vehicle.setCapacityKg(request.getCapacityKg());
	    vehicle.setFuelType(request.getFuelType());
	    vehicle.setMaintenanceStatus(false);
	
	    Vehicle savedVehicle = vehicleRepository.save(vehicle);
	    return mapToVehicleResponse(savedVehicle);
	}
	
	@Override
	public DriverResponseDTO onboardDriver(DriverRequestDTO request) {
	    Driver driver = new Driver();
	    driver.setFullName(request.getFullName());
	    driver.setLicenseNumber(request.getLicenseNumber());
	    driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
	    driver.setDailyShiftHoursLimit(request.getDailyShiftHoursLimit());
	
	    Driver savedDriver = driverRepository.save(driver);
	    return mapToDriverResponse(savedDriver);
	}
	
	@Override
	public VehicleResponseDTO updateMaintenanceStatus(Long vehicleId, Boolean status) {
	    Vehicle vehicle = vehicleRepository.findById(vehicleId)
	            .orElseThrow(() ->
	                    new RuntimeException("Vehicle not found with ID: " + vehicleId));
	
	    vehicle.setMaintenanceStatus(status);
	
	    return mapToVehicleResponse(vehicleRepository.save(vehicle));
	}

	@Override
	public DriverResponseDTO assignDriverToVehicle(Long driverId, Long vehicleId) {
	
	    Driver driver = driverRepository.findById(driverId)
	            .orElseThrow(() ->
	                    new RuntimeException("Driver not found with ID: " + driverId));
	
	    Vehicle vehicle = vehicleRepository.findById(vehicleId)
	            .orElseThrow(() ->
	                    new RuntimeException("Vehicle not found with ID: " + vehicleId));
	
	    if (Boolean.TRUE.equals(vehicle.getMaintenanceStatus())) {
	        throw new IllegalStateException(
	                "Operation failed: Vehicle "
	                        + vehicle.getLicensePlate()
	                        + " is currently under maintenance!");
	    }
	
	    if (driver.getLicenseExpiryDate().isBefore(LocalDate.now())) {
	        throw new IllegalStateException(
	                "Operation failed: Driver "
	                        + driver.getFullName()
	                        + " has an expired commercial license!");
	    }
	
	    if (driver.getVehicle() != null) {
	        driver.getVehicle().setDriver(null);
	    }
	
	    vehicle.setDriver(driver);
	    driver.setVehicle(vehicle);
	
	    driverRepository.save(driver);
	    vehicleRepository.save(vehicle);
	
	    return mapToDriverResponse(driver);
	}

	@Override
	public List<VehicleResponseDTO> getAvailableVehicles() {
	    return vehicleRepository
	            .findByMaintenanceStatusFalseAndDriverIsNull()
	            .stream()
	            .map(this::mapToVehicleResponse)
	            .collect(Collectors.toList());
	}

	@Override
	@Transactional
	public DeliveryTaskResponseDTO createTask(DeliveryTaskRequestDTO request) {
	    DeliveryTask task = new DeliveryTask();
	
	    task.setDestinationAddress(request.getDestinationAddress());
	    task.setLatitude(request.getLatitude());
	    task.setLongitude(request.getLongitude());
	    task.setPackageWeightKg(request.getPackageWeightKg());
	    task.setStatus(com.infotact.fleet.model.TaskStatus.UNASSIGNED);
	    
	    DeliveryTask savedTask = deliveryTaskRepository.save(task);
	
	    return new DeliveryTaskResponseDTO(
	    		savedTask.getId(),
	    		savedTask.getDestinationAddress(),
	    		savedTask.getLatitude(),
	    		savedTask.getLongitude(),
	    		savedTask.getPackageWeightKg(),
	    		savedTask.getStatus(),
	            null,
	            java.time.LocalDateTime.now(),
	            java.time.LocalDateTime.now()
	    );
	}

	private VehicleResponseDTO mapToVehicleResponse(Vehicle vehicle) {
	    return new VehicleResponseDTO(
	            vehicle.getId(),
	            vehicle.getLicensePlate(),
	            vehicle.getCapacityKg(),
	            vehicle.getFuelType(),
	            vehicle.getMaintenanceStatus(),
	            vehicle.getDriver() != null
	                    ? vehicle.getDriver().getId()
	                    : null,
	            vehicle.getCreatedAt(),
	            vehicle.getUpdatedAt()
	    );
	}
	
	private DriverResponseDTO mapToDriverResponse(Driver driver) {
	    return new DriverResponseDTO(
	            driver.getId(),
	            driver.getFullName(),
	            driver.getLicenseNumber(),
	            driver.getLicenseExpiryDate(),
	            driver.getDailyShiftHoursLimit(),
	            driver.getVehicle() != null
	                    ? driver.getVehicle().getLicensePlate()
	                    : null,
	            driver.getCreatedAt(),
	            driver.getUpdatedAt()
	    );
	}


}
