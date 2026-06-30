package com.infotact.fleet.exception;

import com.infotact.fleet.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;

@RestControllerAdvice // 🎯 INTERCEPTOR FILTER: Hooks directly into Spring's web request processing pipeline to catch specific exceptions globally
public class MapProviderExceptionHandler {

    /**
     * Catches and formats WebClient exceptions thrown during outbound API exchanges
     * (e.g., OSRM server down, authentication failure, or HTTP 429 Rate Limits).
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponseDTO> handleMapProviderNetworkException(WebClientResponseException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String errorMessage;

        // 🔍 Categorize error status rules dynamically
        if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            errorMessage = "Map provider rate limit exceeded (HTTP 429). Please throttle outbound routing requests.";
        } else if (status.is5xxServerError()) {
            errorMessage = "Third-party OSRM routing server is currently experiencing an outage. Fallback mode suggested.";
        } else if (status.equals(HttpStatus.UNAUTHORIZED) || status.equals(HttpStatus.FORBIDDEN)) {
            errorMessage = "Invalid API keys or bad security credentials provided to the routing network provider.";
        } else {
            errorMessage = "Outbound Map API communications breakdown: " + ex.getStatusText();
        }

        ErrorResponseDTO errorPayload = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                "External Routing Provider Error",
                errorMessage,
                "/api/routes/optimize" // Tracks context target
        );

        return new ResponseEntity<>(errorPayload, status);
    }

    /**
     * Catches instances where WebClient timeouts expire before the map server responds.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleNetworkTimeoutException(IllegalStateException ex) {
        // Only target timeout strings to avoid swallowing internal state exceptions
        if (ex.getMessage() != null && ex.getMessage().contains("Timeout")) {
            ErrorResponseDTO errorPayload = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.GATEWAY_TIMEOUT.value(),
                    "Network Gateway Timeout",
                    "The external mapping server took too long to return the distance matrix data.",
                    "/api/routes/optimize"
            );
            return new ResponseEntity<>(errorPayload, HttpStatus.GATEWAY_TIMEOUT);
        }
        
        throw ex; // Re-throw if it's an unrelated internal IllegalStateException
    }
}