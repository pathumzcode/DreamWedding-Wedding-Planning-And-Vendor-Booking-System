package com.wedding.dreamwedding.dto;

import com.wedding.dreamwedding.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequest DTO — the data the frontend sends to POST /api/auth/register.
 * All fields are validated using Jakarta Bean Validation annotations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** User's first name — required */
    @NotBlank(message = "First name is required")
    private String firstName;

    /** User's last name — required */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /** User's phone number — required */
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    /** Email — must be valid format and is required */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    /** Password — minimum 6 characters */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** Role — must be CUSTOMER, VENDOR, ADMIN, or HOTEL */
    @NotNull(message = "Role is required")
    private Role role;
}
