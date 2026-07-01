package com.infotact.fleet.model;

public enum TaskStatus {
	UNASSIGNED,  // Task is logged but not tied to a vehicle journey manifest
	ASSIGNED,
    DISPATCHED,  // Bound to an active vehicle, waiting for engine ignition
    IN_TRANSIT,  // Cargo payload is actively moving on the highway network
    DELIVERED    // Drop-off successfully acknowledged by client geofence confirmation
}
