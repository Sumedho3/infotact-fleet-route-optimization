package com.infotact.fleet.service;

import java.util.List;

import com.infotact.fleet.dto.DriverAssignmentRequestDTO;
import com.infotact.fleet.entity.RouteManifest;

public interface ManifestWorkflowService {

    /**
     * Links an authenticated driver profile asset to an existing optimized route manifest row.
     *
     * @param request Payload containing the target manifest ID and driver ID.
     * @return The updated RouteManifest entity with driver credentials attached.
     */
    RouteManifest assignDriverToManifest(DriverAssignmentRequestDTO request);

    /**
     * Fetches a specific route manifest record by its unique database primary identifier.
     *
     * @param manifestId Unique identifier of the target manifest.
     * @return The complete RouteManifest entity profile.
     */
    RouteManifest getManifestById(Long manifestId);

    /**
     * Retrieves all manifests currently stored in the database matching a status filter.
     *
     * @param status String literal flag representing the target phase.
     * @return A list of matching route manifest records.
     */
    List<RouteManifest> getManifestsByStatus(String status);
}