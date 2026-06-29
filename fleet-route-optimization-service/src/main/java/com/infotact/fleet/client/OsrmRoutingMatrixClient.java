package com.infotact.fleet.client;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.OsrmMatrixResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OsrmRoutingMatrixClient {

    @Autowired
    private WebClient routingWebClient;

    /**
     * Sends a synchronous HTTP GET request to the OSRM Table API to retrieve 
     * a calculated road network distance and travel duration matrix for the given waypoints.
     *
     * @param waypoints List of delivery tasks containing coordinate pairs
     * @return The parsed OSRM response containing raw distance and duration matrices
     */
    public OsrmMatrixResponseDTO fetchRoutingMatrices(List<DeliveryTaskResponseDTO> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            throw new IllegalArgumentException("Waypoint list cannot be empty for matrix calculations.");
        }

        // 1. Format coordinates into OSRM URL format: {longitude},{latitude};{longitude},{latitude}...
        // ⚠️ CRITICAL NOTE: OSRM expects Longitude FIRST, then Latitude!
        String coordinateString = waypoints.stream()
                .map(wp -> wp.getLongitude() + "," + wp.getLatitude())
                .collect(Collectors.joining(";"));

        // 2. Build the target URL path for OSRM Table Service
        // Request format example: /table/v1/driving/73.73,18.57;73.74,18.58?annotations=distance,duration
        String uriPath = "/table/v1/driving/" + coordinateString + "?annotations=distance,duration";

        // 3. Execute the outbound HTTP exchange using non-blocking WebClient, 
        // but block synchronously here since our transaction requires the data immediately.
        return routingWebClient.get()
                .uri(uriPath)
                .retrieve()
                .bodyToMono(OsrmMatrixResponseDTO.class)
                .block(); // Blocks execution thread safely until JSON is mapped to Java object
    }
}