package com.infotact.fleet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(name = "capacity_kg", nullable = false)
    private Double capacityKg; // Volumetric/weight cargo capacity limit tracking

    @Column(name = "fuel_type", nullable = false, length = 30)
    private String fuelType;

    @Column(name = "maintenance_status", nullable = false)
    private Boolean maintenanceStatus = false; // Flags if the truck needs service
}
