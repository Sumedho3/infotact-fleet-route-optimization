package com.infotact.fleet.controller;

import com.infotact.fleet.dto.DeliveryTaskRequestDTO;
import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.DriverResponseDTO;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.service.FleetService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogisticsController.class) // 🎯 Slices into only the logistics and dispatch controller layer
class LogisticsControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simulates REST endpoint execution

    @Autowired
    private ObjectMapper objectMapper; // Serializes payload DTO objects to JSON strings

    @MockitoBean
    private FleetService fleetService; // Mock stub mapping dependency

    @Test
    void shouldCreateDeliveryTaskWhenPayloadIsValid() throws Exception {
        // Arrange - Valid payload matching Day 5 constraint requirements
        DeliveryTaskRequestDTO request = new DeliveryTaskRequestDTO("123 Warehouse St, Hinjavadi", 18.57, 73.73, 250.0);
        DeliveryTaskResponseDTO mockResponse = new DeliveryTaskResponseDTO(
                1L, "123 Warehouse St, Hinjavadi", 18.57, 73.73, 250.0, TaskStatus.UNASSIGNED, null, LocalDateTime.now(), LocalDateTime.now()
        );

        Mockito.when(fleetService.createTask(Mockito.any(DeliveryTaskRequestDTO.class)))
               .thenReturn(mockResponse);

        // Act & Assert - Verify 201 Created and response mapping integrity
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.destinationAddress").value("123 Warehouse St, Hinjavadi"))
                .andExpect(jsonPath("$.status").value("UNASSIGNED"));
    }

    @Test
    void shouldRejectTaskCreationWhenCoordinatesAreInvalid() throws Exception {
        // Arrange - Latitude 120.0 breaks our @Max(90) validation constraint engine
        DeliveryTaskRequestDTO invalidRequest = new DeliveryTaskRequestDTO("Invalid Location", 120.0, 73.73, 50.0);

        // Act & Assert - Interceptor advice must return 400 Bad Request response code
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkStatusWhenAssetAssignmentIsTriggered() throws Exception {
        // Arrange - Setup a mock response for a successful pairing link mapping
        DriverResponseDTO mockDriverResponse = new DriverResponseDTO(10L, "Sumedh", "LIC12345", null, 8, "MH-12-QE-1234", null, null);
        
        Mockito.when(fleetService.assignDriverToVehicle(10L, 20L))
               .thenReturn(mockDriverResponse);

        // Act & Assert - Target Member C's explicit path variable endpoint mapping handler
        mockMvc.perform(put("/api/vehicles/20/assign/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedVehiclePlate").value("MH-12-QE-1234"));
    }
}