package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "DriverRequest",
        description = "Inbound form tracking parameters to register active fleet operational operators"
)
public class DriverRequestDTO {

    @Schema(
        description = "Complete official legal first and last name of the driver personnel", 
        example = "Sumit Main", 
        maxLength = 100, 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Driver full name cannot be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String fullName;

    @Schema(
        description = "Unique official regulatory serial alphanumeric commercial driving license registration sequence", 
        example = "DL-MH1420240089", 
        maxLength = 50, 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "License number cannot be blank")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @Schema(
        description = "Official calendar date tracking when the driver's current license validation credentials expire", 
        example = "2031-12-31", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "License expiry date is required")
    private LocalDate licenseExpiryDate;

    @Schema(
        description = "Maximum working shift duration tracking constraint threshold in hours per single workday profile", 
        example = "8", 
        minimum = "4", 
        maximum = "16", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Daily shift hour threshold is required")
    @Min(value = 4, message = "Daily shift limit cannot be less than 4 hours")
    @Max(value = 16, message = "Daily shift limit cannot exceed 16 hours for safety regulatory compliance")
    private Integer dailyShiftHoursLimit;
}