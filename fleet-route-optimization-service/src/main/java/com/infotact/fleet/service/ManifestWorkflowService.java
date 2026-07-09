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
     * 🧩 CORE ORCHESTRATION COMPILATION:
     * Fully compiles an optimized manifest path layout, binds an available driver profile,
     * updates the root lifecycle status, and prepares the batch for warehouse dispatch.
     *
     * @param manifestId The target manifest ID requiring final consolidation.
     * @param driverId The driver asset ID being bound to this itinerary sequence.
     * @return The fully aggregated, saved RouteManifest entity.
     */
    RouteManifest compileAndLinkManifest(Long manifestId, Long driverId);

    /**
     * 🚀 DISPATCH FINALIZATION STEP:
     * Authoritatively finalizes a route manifest lifecycle, recording chronological
     * departure timestamps and triggering underlying stop state cascades.
     *
     * @param manifestId The target manifest record key to release.
     * @return The updated RouteManifest entity.
     */
    RouteManifest finalizeDispatch(Long manifestId);

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