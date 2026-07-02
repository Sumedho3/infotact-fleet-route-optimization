package com.infotact.fleet.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.OsrmMatrixResponseDTO;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OsrmRoutingMatrixClientTest {

    private MockWebServer mockWebServer;
    private OsrmRoutingMatrixClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Initialize the mock local server instance
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        // 2. Build a local WebClient pointed directly at our mock server's dynamic port
        WebClient testWebClient = WebClient.builder()
                .baseUrl(mockWebServer.url("").toString())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        // 3. Instantiate the target client and inject our local test WebClient bean
        this.client = new OsrmRoutingMatrixClient();
        this.objectMapper = new ObjectMapper();

        // Inject the web client field using Spring reflection helpers
        ReflectionTestUtils.setField(client, "routingWebClient", testWebClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Safe shutdown of our virtual local web server session
        this.mockWebServer.shutdown();
    }

    @Test
    void verifyOutboundExchange_ParsesValidJsonPayloadFromNetworkStream() throws Exception {
        // 1. Arrange: Prepare mock coordinate waypoint arrays
        DeliveryTaskResponseDTO task = new DeliveryTaskResponseDTO();
        task.setId(1L);
        task.setLongitude(73.8567);
        task.setLatitude(18.5204);
        List<DeliveryTaskResponseDTO> waypoints = Collections.singletonList(task);

        // 2. Arrange: Set up expected response data grids
        double[][] distances = {{0.0}};
        double[][] durations = {{0.0}};
        OsrmMatrixResponseDTO expectedBody = new OsrmMatrixResponseDTO();
        expectedBody.setDistances(distances);
        expectedBody.setDurations(durations);
        String mockJsonPayload = objectMapper.writeValueAsString(expectedBody);

        // Enqueue the mock response onto our local web server pool
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockJsonPayload)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(200));

        // 3. Act: Trigger the live call inside our OSRM client container
        OsrmMatrixResponseDTO actualResult = client.fetchRoutingMatrices(waypoints);

        // 4. Assert: Verify the reactive mapping layers parsed the body correctly
        assertNotNull(actualResult);
        assertEquals(1, actualResult.getDistances().length);
        assertEquals(0.0, actualResult.getDistances()[0][0]);

        // 5. Assert: Verify the outbound URL formatting was formatted correctly
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/table/v1/driving/73.8567,18.5204?annotations=distance,duration", recordedRequest.getPath());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.ACCEPT));
    }
}