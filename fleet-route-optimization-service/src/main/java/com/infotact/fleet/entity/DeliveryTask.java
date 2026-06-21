package com.infotact.fleet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "delivery_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destination_address", nullable = false, columnDefinition = "TEXT")
    private String destinationAddress;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "package_weight_kg", nullable = false)
    private Double packageWeightKg;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "UNASSIGNED";
}