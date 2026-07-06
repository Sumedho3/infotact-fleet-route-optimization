package com.infotact.fleet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverAssignmentRequestDTO {

    @NotNull(message = "Manifest identification ID cannot be null.")
    private Long manifestId;

    @NotNull(message = "Driver identification ID allocation cannot be null.")
    private Long driverId;
}