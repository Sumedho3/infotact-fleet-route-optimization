package com.infotact.fleet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDTO {
    private String fullName;
    private String licenseNumber;
    private LocalDate licenseExpiryDate;
    private Integer dailyShiftHoursLimit;
}