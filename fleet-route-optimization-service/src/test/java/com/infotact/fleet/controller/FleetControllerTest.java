package com.infotact.fleet.controller;

import com.infotact.fleet.dto.VehicleRequestDTO;
import com.infotact.fleet.dto.VehicleResponseDTO;
import com.infotact.fleet.service.FleetService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FleetController.class)
class FleetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FleetService fleetService;

    @Test
    void shouldOnboardVehicleSuccessfullyWhenPayloadIsValid() throws Exception {

        VehicleRequestDTO request =
                new VehicleRequestDTO("MH-12-QE-1234", 1500.0, "DIESEL");

        VehicleResponseDTO mockResponse =
                new VehicleResponseDTO(
                        1L,
                        "MH-12-QE-1234",
                        1500.0,
                        "DIESEL",
                        false,
                        null,
                        null,
                        null
                );

        Mockito.when(fleetService.onboardVehicle(Mockito.any(VehicleRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value("MH-12-QE-1234"))
                .andExpect(jsonPath("$.fuelType").value("DIESEL"));
    }

    @Test
    void shouldRejectVehicleOnboardingWhenLicensePlateIsBlank() throws Exception {

        VehicleRequestDTO invalidRequest =
                new VehicleRequestDTO("", 1500.0, "DIESEL");

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}