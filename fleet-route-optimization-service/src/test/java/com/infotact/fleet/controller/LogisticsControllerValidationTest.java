package com.infotact.fleet.controller;

import com.infotact.fleet.exception.MapProviderExceptionHandler;
import com.infotact.fleet.service.RouteLogisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogisticsControllerValidationTest {

    private MockMvc mockMvc;

    @Mock
    private RouteLogisticsService routeLogisticsService;

    @InjectMocks
    private RouteOptimizationController routeOptimizationController;

    @BeforeEach
    void setUpStandaloneEnvironment() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(routeLogisticsService);

        // Standalone builder including the controller advice to intercept validations cleanly
        this.mockMvc = MockMvcBuilders.standaloneSetup(routeOptimizationController)
                .setControllerAdvice(new MapProviderExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("🛑 Validation Border: Empty optimization request payload mapping must be blocked at the API gateway")
    void testLinkDriverValidationRejection() throws Exception {
        // Act & Assert: Passing an empty object '{}' violates @NotNull and @NotEmpty constraints
        mockMvc.perform(post("/api/routes/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) 
                .andExpect(status().isBadRequest()); // 400 Bad Request verified at the framework border
    }

    @Test
    @DisplayName("🛑 Validation Border: Malformed URL input paths must trigger immediate type mismatch rejection")
    void testMalformedDispatchInputRejection() throws Exception {
        // Act & Assert: Passing a bad parameter where an object structure is required
        mockMvc.perform(post("/api/routes/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-a-json-string"))
                .andExpect(status().isBadRequest());
    }
}