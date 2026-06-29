package com.infotact.fleet.service.routing;

import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MockRoutingMatrixEngineImpl implements RoutingMatrixEngine {

    /**
     * Generates a dynamic distance matrix using straight-line coordinate geometry
     * to simulate real-world physical distances between delivery nodes.
     */
    @Override
    public double[][] calculateDistanceMatrix(List<DeliveryTaskResponseDTO> waypoints) {
        int size = waypoints.size();
        double[][] distanceMatrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0.0;
                } else {
                    distanceMatrix[i][j] = calculateEuclideanDistance(waypoints.get(i), waypoints.get(j));
                }
            }
        }
        return distanceMatrix;
    }

    /**
     * Generates a dynamic travel time matrix by converting simulated kilometers
     * into minutes based on a standard delivery fleet speed baseline.
     */
    @Override
    public double[][] calculateTravelTimeMatrix(List<DeliveryTaskResponseDTO> waypoints) {
        int size = waypoints.size();
        double[][] timeMatrix = new double[size][size];
        double[][] distanceMatrix = calculateDistanceMatrix(waypoints);

        double averageSpeedKmPerMin = 40.0 / 60.0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                timeMatrix[i][j] = distanceMatrix[i][j] / averageSpeedKmPerMin;
            }
        }
        return timeMatrix;
    }

    /**
     * Helper geometric utility calculating the Euclidean distance
     * between two coordinate nodes.
     */
    private double calculateEuclideanDistance(DeliveryTaskResponseDTO nodeA, DeliveryTaskResponseDTO nodeB) {
        double deltaLat = nodeA.getLatitude() - nodeB.getLatitude();
        double deltaLon = nodeA.getLongitude() - nodeB.getLongitude();

        double coordinateDistance =
                Math.sqrt((deltaLat * deltaLat) + (deltaLon * deltaLon));

        return coordinateDistance * 111.0;
    }
}