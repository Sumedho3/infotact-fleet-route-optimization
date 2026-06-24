package com.infotact.fleet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Driver full name cannot be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "License number cannot be blank")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @NotNull(message = "License expiry date is required")
    private LocalDate licenseExpiryDate;

    @NotNull(message = "Daily shift hour threshold is required")
    @Min(value = 4, message = "Daily shift limit cannot be less than 4 hours")
    @Max(value = 16, message = "Daily shift limit cannot exceed 16 hours for safety regulatory compliance")
    private Integer dailyShiftHoursLimit;
}