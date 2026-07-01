package com.infotact.fleet.util;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🗺️ GEOGRAPHIC UTILITY UTILS:
 * Provides stateless helper operations to format and encode spatial coordinates 
 * into API-compliant string sequences.
 */
public final class CoordinateEncoderUtils {

    // Suppress default constructor to enforce pure stateless static usage
    private CoordinateEncoderUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Formats a collection of tasks into an OSRM-compatible coordinate path string.
     * ⚠️ CRITICAL NOTE: OSRM strictly expects Longitude FIRST, then Latitude!
     * * @param waypoints List of delivery tasks containing geographic pins.
     * @return A formatted path string (e.g., "73.6845,18.5812;73.7621,18.5984")
     */
    public static String formatWaypointsToUri(List<DeliveryTaskResponseDTO> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            throw new IllegalArgumentException("Cannot generate routing URI path from an empty waypoint list.");
        }

        return waypoints.stream()
                .map(wp -> String.format(java.util.Locale.US, "%.6f,%.6f", wp.getLongitude(), wp.getLatitude()))
                .collect(Collectors.joining(";"));
    }
}