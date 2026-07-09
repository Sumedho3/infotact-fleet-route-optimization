package com.infotact.fleet.service.routing;

import com.infotact.fleet.client.OsrmRoutingMatrixClient;
import com.infotact.fleet.dto.DeliveryTaskResponseDTO;
import com.infotact.fleet.dto.OsrmMatrixResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 🔌 This is now your LIVE production engine!
public class OsrmRoutingMatrixEngineImpl implements RoutingMatrixEngine {

    @Autowired
    private OsrmRoutingMatrixClient osrmClient;

    @Override
    public double[][] calculateDistanceMatrix(List<DeliveryTaskResponseDTO> waypoints) {
        OsrmMatrixResponseDTO response = osrmClient.fetchRoutingMatrices(waypoints);
        double[][] rawDistances = response.getDistances();
        
        // 🔄 Convert Meters to Kilometers
        for (int i = 0; i < rawDistances.length; i++) {
            for (int j = 0; j < rawDistances[i].length; j++) {
                rawDistances[i][j] = rawDistances[i][j] / 1000.0;
            }
        }
        return rawDistances;
    }

    @Override
    public double[][] calculateTravelTimeMatrix(List<DeliveryTaskResponseDTO> waypoints) {
        OsrmMatrixResponseDTO response = osrmClient.fetchRoutingMatrices(waypoints);
        double[][] rawDurations = response.getDurations();
        
        // 🔄 Convert Seconds to Minutes
        for (int i = 0; i < rawDurations.length; i++) {
            for (int j = 0; j < rawDurations[i].length; j++) {
                rawDurations[i][j] = rawDurations[i][j] / 60.0;
            }
        }
        return rawDurations;
    }
}