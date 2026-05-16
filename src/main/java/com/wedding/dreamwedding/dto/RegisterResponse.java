package com.wedding.dreamwedding.dto;

import com.wedding.dreamwedding.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterResponse DTO — what the API returns after a successful registration.
 * Does NOT include the password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    /** The MongoDB-generated ID of the newly created user */
    private String id;

    /** Full name for display */
    private String firstName;
    private String lastName;

    /** Registered email */
    private String email;

    /** Phone number */
    private String phoneNumber;

    /** Role assigned to this user */
    private Role role;

    /** Success message */
    private String message;
}
