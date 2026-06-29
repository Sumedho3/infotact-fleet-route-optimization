package com.infotact.fleet.repository;

import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository // 🎯 THE DATA ACCESS HOOK: Registers this component as a data lookup mechanism in the application context
public interface RouteManifestRepository extends JpaRepository<RouteManifest, Long> {

    /**
     * DERIVED QUERY HOOK: 
     * Finds all historical optimized route logs associated with a specific vehicle asset.
     * Spring Data translates this to: SELECT * FROM route_manifests WHERE vehicle_id = ?
     */
    List<RouteManifest> findByVehicle(Vehicle vehicle);

    /**
     * DERIVED QUERY HOOK: 
     * Finds all optimized routes generated within a specific timeframe window.
     * Spring Data translates this to: SELECT * FROM route_manifests WHERE created_at BETWEEN ? AND ?
     */
    List<RouteManifest> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * DERIVED QUERY HOOK:
     * Combines parameters to pull historical route sheets for a specific vehicle on a given date grid.
     */
    List<RouteManifest> findByVehicleAndCreatedAtBetween(Vehicle vehicle, LocalDateTime startDateTime, LocalDateTime endDateTime);
}