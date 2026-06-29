package com.infotact.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "route_manifests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteManifest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "total_distance_km", nullable = false)
    private Double totalDistanceKm;

    @Column(name = "total_duration_minutes", nullable = false)
    private Double totalDurationMinutes;

    // 🎯 ONE-TO-MANY RELATIONSHIP UNIFIED LINK:
    // Tracks the ordered collection of delivery tasks assigned to this manifest run.
    // We add an explicit order column to preserve the exact sequence calculated by the engine!
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "route_manifest_id")
    @OrderColumn(name = "stop_sequence_index")
    private List<DeliveryTask> optimizedStops = new ArrayList<>();
}