package com.wedding.dreamwedding.dto;

import com.wedding.dreamwedding.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse DTO — what the API returns after a successful login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
    private boolean profileCompleted;
    private String profilePicture;
    private String message;
}
