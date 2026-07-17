package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "DriverResponse",
        description = "Data transfer object detailing active professional personnel shift profiles"
)
public class DriverResponseDTO {

    @Schema(description = "Unique sequence identifier allocated to the driver operator inside the system schema layer", example = "301")
    private Long id;

    @Schema(description = "Complete official legal name profile registered to this personnel record", example = "Sumit Main")
    private String fullName;

    @Schema(description = "Unique alphanumeric commercial driving license string belonging to the operator", example = "DL-MH1420240089")
    private String licenseNumber;

    @Schema(description = "Calendar date marking the conclusion of the driver's legal operations eligibility threshold", example = "2031-12-31")
    private LocalDate licenseExpiryDate;

    @Schema(description = "Configured regulatory maximum shift capability hour limit assigned to the driver", example = "8")
    private Integer dailyShiftHoursLimit;

    @Schema(description = "Unique high-priority vehicle license alphanumeric identification plate tracking tag actively bound to this operator context", example = "MH-12-QW-5678")
    private String assignedVehiclePlate;

    @Schema(description = "System isolation creation timestamp tracing the enrollment origin of the profile", example = "2026-01-15T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "System adjustment timestamp marking the latest configuration shift applied to the operator values", example = "2026-07-17T15:36:00")
    private LocalDateTime updatedAt;
}