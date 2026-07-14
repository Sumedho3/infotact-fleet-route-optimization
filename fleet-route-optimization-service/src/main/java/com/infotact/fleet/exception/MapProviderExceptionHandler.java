package com.infotact.fleet.exception;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.infotact.fleet.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class MapProviderExceptionHandler {

    /**
     * Catches and formats WebClient exceptions thrown during outbound API exchanges.
     * Updated to match the uniform Week 3 ErrorResponseDTO constructor strategy.
     */
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Geocoding Payload Fault (Invalid address data structure).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity - Geographic coordinates do not exist in the map subsystem.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests - Outbound map provider rate limit exceeded threshold filters.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error - Third-party OSRM routing engine server outage encountered.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponseDTO> handleMapProviderNetworkException(WebClientResponseException ex, HttpServletRequest request) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String errorMessage;
        
        if (status.value() == 400 || status.value() == 422) {
            errorMessage = "Geocoding Payload Fault: The address provided is invalid or the coordinates do not exist in the geographic subsystem.";
        }
        else if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            errorMessage = "Map provider rate limit exceeded (HTTP 429). Please throttle outbound routing requests.";
        } else if (status.is5xxServerError()) {
            errorMessage = "Third-party OSRM routing server is currently experiencing an outage. Fallback mode suggested.";
        } else if (status.equals(HttpStatus.UNAUTHORIZED) || status.equals(HttpStatus.FORBIDDEN)) {
            errorMessage = "Invalid API keys or bad security credentials provided to the routing network provider.";
        } else {
            errorMessage = "Outbound Map API communications breakdown: " + ex.getStatusText();
        }

        // 🎯 FIX: Adjusted to include the standard HttpStatus reason phrase and dynamic request URI path
        ErrorResponseDTO errorPayload = new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(), 
                errorMessage,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorPayload, status);
    }

    /**
     * Catches instances where WebClient timeouts expire before the map server responds.
     */
    @ApiResponse(
            responseCode = "504",
            description = "Gateway Timeout - The external routing network provider failed to respond in time.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleNetworkTimeoutException(IllegalStateException ex, HttpServletRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains("Timeout")) {
            
            // 🎯 FIX: Adjusted to pass dynamic pathing and clean HTTP string parameters uniform with the application layout
            ErrorResponseDTO errorPayload = new ErrorResponseDTO(
                    HttpStatus.GATEWAY_TIMEOUT.value(),
                    HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
                    "The external mapping server took too long to return the distance matrix data.",
                    request.getRequestURI()
            );
            return new ResponseEntity<>(errorPayload, HttpStatus.GATEWAY_TIMEOUT);
        }
        
        throw ex;
    }
}