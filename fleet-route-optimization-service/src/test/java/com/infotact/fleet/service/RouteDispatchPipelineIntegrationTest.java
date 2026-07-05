package com.infotact.fleet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotact.fleet.dto.OsrmMatrixResponseDTO;
import com.infotact.fleet.dto.RouteOptimizationRequestDTO;
import com.infotact.fleet.dto.RouteOptimizationResponseDTO;
import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.entity.Vehicle;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import com.infotact.fleet.repository.RouteManifestRepository;
import com.infotact.fleet.repository.VehicleRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class RouteDispatchPipelineIntegrationTest {

    @Autowired
    private RouteLogisticsService routeLogisticsService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;

    @Autowired
    private RouteManifestRepository routeManifestRepository;

    @Autowired
    private Object matrixEngine;

    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {

        this.objectMapper = new ObjectMapper();
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        Object clientComponent =
                ReflectionTestUtils.getField(matrixEngine, "matrixClient");

        if (clientComponent != null) {

            org.springframework.web.reactive.function.client.WebClient testWebClient =
                    org.springframework.web.reactive.function.client.WebClient
                            .builder()
                            .baseUrl(mockWebServer.url("").toString())
                            .build();

            ReflectionTestUtils.setField(
                    clientComponent,
                    "routingWebClient",
                    testWebClient);
        }

        routeManifestRepository.deleteAll();
        deliveryTaskRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("🏁 E2E Integration: Execute full route pipeline from unassigned tasks to final manifest storage")
    void verifyFullRoutePipelineExecutionChain_StoresManifestSuccessfully() throws Exception {

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("MH-14-EU-1234");
        vehicle = vehicleRepository.save(vehicle);

        DeliveryTask task1 = new DeliveryTask();
        task1.setDestinationAddress("Depot Hub, Hinjavadi");
        task1.setLatitude(18.5204);
        task1.setLongitude(73.8567);
        task1.setStatus(TaskStatus.UNASSIGNED);
        task1 = deliveryTaskRepository.save(task1);

        DeliveryTask task2 = new DeliveryTask();
        task2.setDestinationAddress("Wakad Central");
        task2.setLatitude(18.5987);
        task2.setLongitude(73.7456);
        task2.setStatus(TaskStatus.UNASSIGNED);
        task2 = deliveryTaskRepository.save(task2);

        double[][] distances = {
                {0.0, 12.5},
                {12.5, 0.0}
        };

        double[][] durations = {
                {0.0, 22.0},
                {22.0, 0.0}
        };

        OsrmMatrixResponseDTO mockOsrmResponse =
                new OsrmMatrixResponseDTO();

        mockOsrmResponse.setDistances(distances);
        mockOsrmResponse.setDurations(durations);

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(objectMapper.writeValueAsString(mockOsrmResponse))
                        .setHeader(
                                HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(200));

        RouteOptimizationRequestDTO request =
                new RouteOptimizationRequestDTO();

        request.setVehicleId(vehicle.getId());
        request.setDeliveryTaskIds(
                Arrays.asList(task1.getId(), task2.getId()));

        RouteOptimizationResponseDTO response =
                routeLogisticsService.optimizeAndAssignRoute(request);

        assertNotNull(response);
        assertEquals(2, response.getOptimizedStops().size());

        List<RouteManifest> savedManifests =
                routeManifestRepository.findAll();

        assertEquals(
                1,
                savedManifests.size(),
                "Pipeline must store exactly 1 permanent route manifest row entry.");

        RouteManifest manifest = savedManifests.get(0);

        assertEquals(
                vehicle.getId(),
                manifest.getVehicle().getId());

        assertEquals(
                "OPTIMIZED",
                manifest.getStatus());

        DeliveryTask updatedTask1 =
                deliveryTaskRepository.findById(task1.getId())
                        .orElseThrow();

        assertEquals(
                TaskStatus.ASSIGNED,
                updatedTask1.getStatus(),
                "Task state must upgrade to ASSIGNED flag.");
    }
}