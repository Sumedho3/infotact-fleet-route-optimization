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
        return response.getDistances();
    }

    @Override
    public double[][] calculateTravelTimeMatrix(List<DeliveryTaskResponseDTO> waypoints) {
        OsrmMatrixResponseDTO response = osrmClient.fetchRoutingMatrices(waypoints);
        return response.getDurations();
    }
}