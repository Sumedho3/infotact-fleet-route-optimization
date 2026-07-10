package com.infotact.fleet.integration;

import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.entity.Vehicle;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import com.infotact.fleet.repository.RouteManifestRepository;
import com.infotact.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 🔄 Automatic rollback after execution to preserve clean database baseline tracking
class DispatchLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RouteManifestRepository routeManifestRepository;

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Long manifestId;
    private Long task1Id;
    private Long task2Id;

    @BeforeEach
    void setUpSystemState() {
        // 1. Persist an active transport asset
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("MH-14-INT-2026");
        vehicle.setCapacityKg(1000.0);
        vehicle = vehicleRepository.save(vehicle);

        // 2. Persist parent baseline manifest skeleton structure
        RouteManifest manifest = new RouteManifest();
        manifest.setVehicle(vehicle);
        manifest.setStatus(ManifestStatus.OPTIMIZED);
        manifest.setDriverId(99L); // Pre-assign a driver to satisfy workflow safety conditions
        manifest = routeManifestRepository.save(manifest);
        this.manifestId = manifest.getId();

        // 3. Persist matching child delivery items linked straight to the route parent mapping
        DeliveryTask task1 = new DeliveryTask();
        task1.setDestinationAddress("Phase 3, Hinjawadi");
        task1.setLatitude(18.5812);
        task1.setLongitude(73.6845);
        task1.setPackageWeightKg(150.0);
        task1.setStatus(TaskStatus.ASSIGNED);
        task1.setRoute(manifest); // Link relationship mapping target directly
        task1 = deliveryTaskRepository.save(task1);
        this.task1Id = task1.getId();

        DeliveryTask task2 = new DeliveryTask();
        task2.setDestinationAddress("Baner High Street");
        task2.setLatitude(18.5594);
        task2.setLongitude(73.7798);
        task2.setPackageWeightKg(85.0);
        task2.setStatus(TaskStatus.ASSIGNED);
        task2.setRoute(manifest);
        task2 = deliveryTaskRepository.save(task2);
        this.task2Id = task2.getId();
        
        // Finalize bidirectional sync mapping for entity relationship validation checks
        List<DeliveryTask> stopsList = new ArrayList<>();
        stopsList.add(task1);
        stopsList.add(task2);
        manifest.setOptimizedStops(stopsList);
        routeManifestRepository.save(manifest);
    }

    @Test
    @DisplayName("🚀 E2E Lifecycle: Triggering dispatch endpoint must modify parent records and cascade statuses to tasks")
    void testEndToEndDispatchWorkflowExecutionStream() throws Exception {
        
        // Act: Execute the concrete POST API dispatch call via real MockMvc network filters
        mockMvc.perform(post("/api/manifests/{id}/dispatch", manifestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DISPATCHED")))
                .andExpect(jsonPath("$.driverId", is(99)));

        // Assert: Read fresh database updates to verify transactional atomic updates passed cleanly
        RouteManifest updatedManifest = routeManifestRepository.findById(manifestId).orElseThrow();
        assertEquals(ManifestStatus.DISPATCHED, updatedManifest.getStatus(), "Parent manifest state must be DISPATCHED.");
        assertNotNull(updatedManifest.getDispatchedAt(), "Parent dispatch departure timestamp must be populated.");

        // Assert: Verify cascading state propagation engine successfully swept through child database rows
        DeliveryTask databaseTask1 = deliveryTaskRepository.findById(task1Id).orElseThrow();
        DeliveryTask databaseTask2 = deliveryTaskRepository.findById(task2Id).orElseThrow();

        assertEquals(TaskStatus.DISPATCHED, databaseTask1.getStatus(), "Child DeliveryTask 1 should have cascaded to DISPATCHED.");
        assertEquals(TaskStatus.DISPATCHED, databaseTask2.getStatus(), "Child DeliveryTask 2 should have cascaded to DISPATCHED.");
    }
}

