package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(
        name = "DriverAssignmentRequest",
        description = "Inbound matrix mapping parameters linking an available driver asset to a physical vehicle container"
)

public class DriverAssignmentRequestDTO {

    @NotNull(message = "Manifest identification ID cannot be null.")
    private Long manifestId;

    @NotNull(message = "Driver identification ID allocation cannot be null.")
    private Long driverId;
}