package com.infotact.fleet.exception;

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
     */
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
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleNetworkTimeoutException(IllegalStateException ex, HttpServletRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains("Timeout")) {
            
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