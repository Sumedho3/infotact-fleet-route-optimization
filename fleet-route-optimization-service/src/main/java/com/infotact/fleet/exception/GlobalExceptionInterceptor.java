package com.infotact.fleet.exception;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.infotact.fleet.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionInterceptor {

	@ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRuleViolation(IllegalStateException ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    
    /**
     * 🛑 HANDLER 1: Catch core business validation state runtime conflicts.
     */
    @ExceptionHandler(DeliveryStateConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleStateConflict(DeliveryStateConflictException ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * 🛑 HANDLER 2: Catch asset resource lookups that do not exist in database records.
     */
    @ExceptionHandler({NoSuchElementException.class, RuntimeException.class})
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = ex instanceof NoSuchElementException ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        
        ErrorResponseDTO error = new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, status);
    }

    /**
     * 🛑 HANDLER 3: Your existing validation method (Keep exactly as you wrote it!)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationFailures(MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String validationErrorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Failed: " + validationErrorMessage,
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 🛑 HANDLER 4: Intercepts client payloads that are completely unparsable or structurally broken.
     * Extracts precise field-level syntax faults to guide frontend correction cycles.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMalformedJsonPayload(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String localizedErrorMessage = "Malformed JSON request payload body structure syntax error.";
        
        // Traverse the exception cause to extract the specific broken field name if available
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException mismatchedInputException) {
            if (!mismatchedInputException.getPath().isEmpty()) {
                String fieldName = mismatchedInputException.getPath().get(0).getFieldName();
                localizedErrorMessage = "Parsing failure on target property field: '" + fieldName + "'. Verified data type mismatch constraint violation.";
            }
        } else if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            localizedErrorMessage = "Required HTTP request payload body structure is missing completely.";
        }

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                localizedErrorMessage,
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 🛑 HANDLER 5: Gracefully catches HTTP 429 errors thrown by downstream third-party location providers.
     * Prevents internal cascading routing thread exhaustion by sending an organized backoff notification.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleDownstreamRateLimits(RateLimitExceededException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.TOO_MANY_REQUESTS.value(), // HTTP 429 Too Many Requests
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                "Downstream Provider Quota Exhaustion: " + ex.getMessage() + " Please initiate exponential retry backoff.",
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    // 🔍 Fallback catch-all for unexpected internal server errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleFallbackInternalError(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected core internal processing error occurred: " + ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
