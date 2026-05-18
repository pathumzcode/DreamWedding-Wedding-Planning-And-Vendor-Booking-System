package com.wedding.dreamwedding.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerProfileUpdateRequest {

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; // optional new password
}
