package com.wedding.dreamwedding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ErrorResponse DTO — standardized error response format for all API errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** HTTP status code (e.g., 400, 409, 500) */
    private int status;

    /** Short error message */
    private String error;

    /** Detailed description of what went wrong */
    private String message;

    /** Timestamp when the error occurred */
    private LocalDateTime timestamp;

    /** Optional field-level validation errors (field name → error message) */
    private Map<String, String> fieldErrors;

    /** Constructor for simple errors without field-level details */
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
