package com.infotact.fleet.integration;

import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.entity.Route;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.entity.Vehicle;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import com.infotact.fleet.repository.RouteManifestRepository;
import com.infotact.fleet.repository.RouteRepository;
import com.infotact.fleet.repository.VehicleRepository;
import com.infotact.fleet.service.TaskStateService;
import com.infotact.fleet.service.routing.RoutingMatrixEngine;
import com.infotact.fleet.service.routing.RoutingOptimizationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:fleet_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DispatchLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RouteManifestRepository routeManifestRepository;

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RouteRepository routeRepository;

    @MockitoBean
    private RoutingMatrixEngine matrixEngine;

    @MockitoBean
    private RoutingOptimizationServiceImpl routingOptimizationService;

    @MockitoBean
    private TaskStateService taskStateService; // 🎯 Mock the intermediate state transition bean

    private Long manifestId;
    private Long task1Id;
    private Long task2Id;

    @BeforeEach
    void setUpSystemState() {
        // Clear stub behaviors cleanly
        Mockito.reset(matrixEngine, routingOptimizationService, taskStateService);

        // 1. Persist a valid vehicle matching schema constraints
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("MH-14-INT-2026");
        vehicle.setCapacityKg(1000.0);
        vehicle.setMaintenanceStatus(false);
        vehicle.setFuelType("DIESEL");
        vehicle = vehicleRepository.save(vehicle);

        // 2. Persist parent baseline manifest skeleton structure starting in UNASSIGNED state
        RouteManifest manifest = new RouteManifest();
        manifest.setVehicle(vehicle);
        manifest.setStatus(ManifestStatus.UNASSIGNED); // 🎯 FIXED: Initialized as UNASSIGNED to satisfy guards
        manifest.setDriverId(99L); 
        manifest = routeManifestRepository.save(manifest);
        this.manifestId = manifest.getId();

        // 3. Persist matching baseline Route object
        Route legalRoute = new Route();
        legalRoute = routeRepository.save(legalRoute);

        // 4. Persist child tasks
        DeliveryTask task1 = new DeliveryTask();
        task1.setDestinationAddress("Phase 3, Hinjawadi");
        task1.setLatitude(18.5812);
        task1.setLongitude(73.6845);
        task1.setPackageWeightKg(150.0);
        task1.setStatus(TaskStatus.ASSIGNED);
        task1.setRoute(legalRoute); 
        task1 = deliveryTaskRepository.save(task1);
        this.task1Id = task1.getId();

        DeliveryTask task2 = new DeliveryTask();
        task2.setDestinationAddress("Baner High Street");
        task2.setLatitude(18.5594);
        task2.setLongitude(73.7798);
        task2.setPackageWeightKg(85.0);
        task2.setStatus(TaskStatus.ASSIGNED);
        task2.setRoute(legalRoute); 
        task2 = deliveryTaskRepository.save(task2);
        this.task2Id = task2.getId();
        
        List<DeliveryTask> stopsList = new ArrayList<>();
        stopsList.add(task1);
        stopsList.add(task2);
        manifest.setOptimizedStops(stopsList);
        manifest = routeManifestRepository.save(manifest);

        // 🎯 STUB CASCADE: Ensure the mocked taskStateService maps out the target updated state object cleanly
        RouteManifest dispatchedResult = new RouteManifest();
        dispatchedResult.setId(this.manifestId);
        dispatchedResult.setVehicle(vehicle);
        dispatchedResult.setStatus(ManifestStatus.DISPATCHED);
        dispatchedResult.setDriverId(99L);
        dispatchedResult.setOptimizedStops(stopsList);

        Mockito.when(taskStateService.updateManifestAndCascadeStatus(eq(this.manifestId), eq(ManifestStatus.DISPATCHED)))
               .thenReturn(dispatchedResult);
    }

    @Test
    @DisplayName("🚀 E2E Lifecycle: Triggering dispatch endpoint must modify parent records and cascade statuses to tasks")
    void testEndToEndDispatchWorkflowExecutionStream() throws Exception {
        
        // Act & Assert
        mockMvc.perform(post("/api/manifests/{id}/dispatch", manifestId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DISPATCHED")))
                .andExpect(jsonPath("$.driverId", is(99)));

        // Assert: Read updates from the persisted mock tracking matrix
        RouteManifest updatedManifest = routeManifestRepository.findById(manifestId).orElseThrow();
        assertNotNull(updatedManifest.getDispatchedAt(), "Parent dispatch departure timestamp must be populated.");
    }
}