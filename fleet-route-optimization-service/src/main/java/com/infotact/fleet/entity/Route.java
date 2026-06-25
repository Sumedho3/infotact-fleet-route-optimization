package com.infotact.fleet.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_distance_km")
    private Double totalDistanceKm = 0.0;

    @Column(name = "total_duration_minutes")
    private Long totalDurationMinutes = 0L;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING";

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DeliveryTask> tasks = new ArrayList<>();

    public void addDeliveryTask(DeliveryTask task) {
        tasks.add(task);
        task.setRoute(this);
    }

    public void removeDeliveryTask(DeliveryTask task) {
        tasks.remove(task);
        task.setRoute(null);
    }
}