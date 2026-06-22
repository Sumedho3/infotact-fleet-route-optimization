package com.infotact.fleet.repository;

import com.infotact.fleet.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByMaintenanceStatusTrue();

    List<Vehicle> findByMaintenanceStatusFalseAndDriverIsNull();

    Optional<Vehicle> findByLicensePlate(String licensePlate);
}