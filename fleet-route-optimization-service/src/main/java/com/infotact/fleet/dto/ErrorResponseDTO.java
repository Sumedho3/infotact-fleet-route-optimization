package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(
        name = "ErrorResponse",
        description = "Standardized format describing system errors encountered at runtime"
)
public class ErrorResponseDTO {
	
    @Schema(description = "The precise application runtime date and time context when the structural interceptor caught the failure", example = "2026-07-17T15:56:15")
	private LocalDateTime timestamp;

    @Schema(description = "Standard HTTP protocol status numeric code reflecting the boundary validation or server response failure", example = "400")
    private int status;

    @Schema(description = "Official HTTP standard error description corresponding directly to the resulting status layer code", example = "Bad Request")
    private String error;

    @Schema(description = "Customized human-readable descriptive text highlighting the functional breakdown or correction vectors needed", example = "Geocoding Payload Fault: The address provided is invalid.")
    private String message;

    @Schema(description = "The exact inbound REST controller endpoint path resource mapping URI targeted by the client engine", example = "/api/v1/routes/optimize")
    private String path;

    @Schema(description = "Collection of micro-validation context logs pinpointing target constraint structural field execution drops", example = "[\"packageWeightKg: Package weight must be a positive number greater than zero\"]")
    private List<String> details;
    
    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

}