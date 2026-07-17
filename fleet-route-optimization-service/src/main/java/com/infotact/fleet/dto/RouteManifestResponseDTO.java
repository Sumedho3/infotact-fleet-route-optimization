package com.infotact.fleet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "RouteManifestResponse",
        description = "Output response payload detailing optimized manifests and assigned fleet assets"
)
public class RouteManifestResponseDTO {

    @Schema(description = "Unique primary sequence identifier tracking the route manifest entry", example = "7052")
    private Long id;

    @Schema(description = "Unique identifier corresponding to the assigned driver personnel operator", example = "301")
    private Long driverId;

    @Schema(description = "Current lifecycle tracking state status of the manifest", example = "ASSIGNED")
    private String status;

    @Schema(description = "Chronological list of algorithmically sorted and fully documented waypoints assigned to the manifest")
    private List<DeliveryTaskResponseDTO> optimizedStops;

    @Schema(description = "System execution timestamp indicating when the manifest departed the warehouse gate context", example = "2026-07-17T18:30:00")
    private LocalDateTime dispatchedAt;
}