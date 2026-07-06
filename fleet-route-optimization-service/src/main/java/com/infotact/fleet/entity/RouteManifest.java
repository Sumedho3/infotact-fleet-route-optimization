package com.infotact.fleet.entity;
import com.infotact.fleet.model.ManifestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "route_manifests")
@Getter
@Setter
public class RouteManifest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "total_distance_km", nullable = false)
    private double totalDistanceKm;

    @Column(name = "total_duration_minutes", nullable = false)
    private double totalDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ManifestStatus status = ManifestStatus.UNASSIGNED;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "manifest_id")
    @OrderColumn(name = "stop_sequence_index")
    private List<DeliveryTask> optimizedStops;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    public void transitionToStatus(ManifestStatus targetStatus) {
        // Run the invariant guard rule evaluation check
        this.status.validateTransitionTo(targetStatus);
        this.status = targetStatus;
    }
}