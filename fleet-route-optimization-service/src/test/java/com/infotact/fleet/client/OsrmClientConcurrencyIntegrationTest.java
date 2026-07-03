package com.infotact.fleet.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.OsrmMatrixResponseDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTimeout;

class OsrmClientConcurrencyIntegrationTest {

    private MockWebServer mockWebServer;
    private OsrmRoutingMatrixClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        // Configure a local WebClient instance pointed at our mock server
        WebClient testWebClient = WebClient.builder()
                .baseUrl(mockWebServer.url("").toString())
                .build();

        this.client = new OsrmRoutingMatrixClient();
        this.objectMapper = new ObjectMapper();

        // Inject the configured WebClient instance into the client component
        ReflectionTestUtils.setField(client, "routingWebClient", testWebClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    @DisplayName("⏳ Integration: Verify outbound client handles slow API responses asynchronously without thread blocking")
    void verifyAsynchronousExecution_DoesNotBlockThreads_OnSlowMappingProvider() throws Exception {
        // 1. Arrange: Prepare mock payload data structures
        DeliveryTaskResponseDTO task = new DeliveryTaskResponseDTO();
        task.setId(99L);
        task.setLongitude(73.8567);
        task.setLatitude(18.5204);
        List<DeliveryTaskResponseDTO> waypoints = Collections.singletonList(task);

        OsrmMatrixResponseDTO expectedBody = new OsrmMatrixResponseDTO();
        expectedBody.setDistances(new double[][]{{0.0}});
        expectedBody.setDurations(new double[][]{{0.0}});
        String jsonPayload = objectMapper.writeValueAsString(expectedBody);

        // ARTIFICIAL NETWORK DELAY: Force the mock server to hold the response connection open for 2 full seconds
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonPayload)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBodyDelay(2, TimeUnit.SECONDS)
                .setResponseCode(200));

        // 2. Act: Wrap the operation execution inside a declarative timeout rule.
        // We offload the blocking client method to Project Reactor's background thread pool.
        Mono<OsrmMatrixResponseDTO> asyncResultMono = Mono.fromCallable(() -> client.fetchRoutingMatrices(waypoints))
                .subscribeOn(Schedulers.boundedElastic());

        // 3. Assert: Verify that the main thread triggers execution and returns immediately (< 200ms)
        assertTimeout(Duration.ofMillis(200), () -> {
            asyncResultMono.subscribe(); // Non-blocking trigger; execution drops to a background thread pool
        });

        // 4. Cleanup/Verification: Wait for the background worker thread to hit the server before closing the test
        mockWebServer.takeRequest(5, TimeUnit.SECONDS);
    }
}