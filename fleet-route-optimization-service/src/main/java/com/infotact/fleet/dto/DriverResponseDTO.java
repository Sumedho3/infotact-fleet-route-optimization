package com.infotact.fleet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDTO {
    private Long id;
    private String fullName;
    private String licenseNumber;
    private LocalDate licenseExpiryDate;
    private Integer dailyShiftHoursLimit;
    private String assignedVehiclePlate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}