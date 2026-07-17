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

    @Schema(
        description = "Unique primary identifier of the route operational dispatch manifest timeline", 
        example = "7052", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Manifest identification ID cannot be null.")
    private Long manifestId;

    @Schema(
        description = "Unique primary identifier corresponding to the personnel operator profile records", 
        example = "301", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Driver identification ID allocation cannot be null.")
    private Long driverId;
}